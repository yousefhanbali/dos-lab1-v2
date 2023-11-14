<?php

namespace App\Http\Controllers;

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
        if ($request->isMethod('PUT')) {
            if ($request->price != null) {
                $book->price = $request->price;
            }
            if ($request->quantity != null) {
                $book->quantity = $request->quantity;
                CsvService::UpdateBook($book);
            }
            return response()->json($book);
        }
    }
}