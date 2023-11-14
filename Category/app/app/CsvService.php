<?php

namespace App;

use App\Models\Book;

class CsvService
{
    const fileName = '../app/db.csv';

    private static $data = [];
    private static $isDataLoaded = false;
    private static function LoadData()
    {
        if (self::$isDataLoaded) {
            return self::$data;
        }
        $file = fopen(self::fileName, "r");
        while (!feof($file)) {
            $temp = fgetcsv($file);
            if ($temp == null)
                continue;
            self::$data[] = $temp;
        }
        fclose($file);
        self::$isDataLoaded = true;
        return self::$data;
    }


    public static function GetBookById($id)
    {
        $data = self::LoadData();
        foreach ($data as $book) {
            if ($book[0] == $id) {
                return new Book($book[0], $book[1], $book[2], intval($book[3]), intval($book[4]));
            }
        }
        return $data;
    }

    public static function GetBooksBySubject($subject)
    {
        $data = self::LoadData();
        $matchingBooks = [];
        foreach ($data as $book) {
            if ($book[2] == $subject) {
                $matchingBooks[] = new Book($book[0], $book[1], $book[2], intval($book[3]), intval($book[4]));
            }
        }
        return $matchingBooks;
    }

    public static function UpdateBook(Book $book)
    {
        $data = self::LoadData();
        foreach ($data as $index => &$bookToUpdate) {
            if ($bookToUpdate[0] == $book->id) {
                $bookToUpdate = [$book->id, $book->title, $book->subject, $book->quantity, $book->price];
                self::$data[$index] = $bookToUpdate;
                return self::WriteData();
            }
        }
        return false;
    }

    private static function WriteData()
    {
        $data = self::LoadData();
        $fp = fopen(self::fileName, 'w');
        rewind($fp);
        foreach ($data as $fields) {
            if ($fields == null)
                continue;
            fputcsv($fp, $fields);
        }
        self::$isDataLoaded = false;
        fclose($fp);
    }
}