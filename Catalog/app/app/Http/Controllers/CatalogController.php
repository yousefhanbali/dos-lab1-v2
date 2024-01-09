<?php

namespace App\Http\Controllers;

use App\CacheService;
use App\CsvService;
use Illuminate\Http\Request;
use Laravel\Lumen\Routing\Controller as BaseController;
use App\Models\Book;
use App;

class CatalogController extends BaseController
{
    public function getBooksBySubject($subject)
    {
        $matchingBooks = CsvService::GetBooksBySubject($subject);

        return response()->json($matchingBooks);
    }

    public function getBookById($id)
    {
        $matchingBook = $this->searchBook($id);
        if ($matchingBook == null) {
            return response('', 404);
        } else {
            return response()->json($matchingBook);
        }
    }


    private function searchBook($id)
    {
        $matchingBook = CsvService::GetBookById($id);
        return $matchingBook;
    }

    public function updateBookById(Request $request, $id)
    {
        $book = $this->searchBook($id);
        if ($book == null) {
            return response('', 404);
        }
        if ($request->isMethod('PUT')) {
            if ($request->price != null) {
                $book->price = $request->price;
                CsvService::UpdateBook($book);
                $this->invalidateCache($book);
                $this->notifyReplica($book);
            }
            if ($request->quantity != null) {
                $book->quantity = $request->quantity;
                CsvService::UpdateBook($book);
                $this->invalidateCache($book);
                $this->notifyReplica($book);
            }
            return response()->json($book);
        }
    }

    public function purchaseBookById(Request $request, $id)
    {
        $book = $this->searchBook($id);
        if ($book == null) {
            return response('', 404);
        }
        if ($request->isMethod('PUT')) {
            if ($book->quantity == 0) {
                return response('Out of stock', 400);
            }

            $book->quantity = $book->quantity - 1;
            CsvService::UpdateBook($book);
            error_log("Invalidating cache");
            $this->invalidateCache($book);
            error_log("Notifying replicas");
            $this->notifyReplica($book);
            return response()->json($book);
        } else {
            return response('', 400);
        }
    }

    public function invalidateCache(Book $book)
    {
        $id = $book->id;
        $frontend_server = getenv('FRONTEND_SERVER');
        $frontend_port = getenv('FRONTEND_PORT');
        $url = "http://$frontend_server:$frontend_port/invalidate/$id";
        $this->sendRequest($url, "DELETE", null);
    }

    public function notifyReplica(Book $book)
    {
        $category = "catalog";
        $port = 8000;
        $hostname = $category; // Replace this with your desired hostname

        $records = dns_get_record($hostname, DNS_A);
        $ips = array();
        if ($records !== false) {

            foreach ($records as $record) {
                if ($record['type'] == 'A') {
                    $ips[] = $record['ip'];
                }
            }
        }
        $serverIP = gethostbyname(gethostname());
        error_log($serverIP);
        foreach ($ips as $value) {
            if ($value == $serverIP)
                continue;
            $url = "http://$value:$port/notify";

            $body = json_encode($book);

            $this->sendRequest($url, "POST", $body);
        }
        // $this->sendRequest($url, "GET");
        // error_log("FINISHED NOTIFYING REPLICA");
    }

    public function receiveNotify(Request $request)
    {
        $book = new Book($request->id, $request->title, $request->subject, $request->quantity, $request->price);
        CsvService::UpdateBook($book);
        error_log("Book $book->id updated");
        return response()->json();
    }

    private function sendRequest($url, $method, $body)
    {
        $options = [
            'http' => [
                'method' => $method,
                'header' => 'Content-type: application/json',
                'content' => $body,
            ]
        ];

        $context = stream_context_create($options);

        $response = file_get_contents($url, false, $context);

        if ($response === false) {
            error_log("Error while making the $method request on $url.");
        }
    }
}