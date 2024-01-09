<?php


namespace App\Models;

use JsonSerializable;

class Book implements JsonSerializable
{
    public $id;
    public $title = '';
    public $quantity = 0;
    public $price = 0;
    public $subject = '';

    public function __construct($id, $title, $subject, $quantity, $price)
    {
        $this->id = $id;
        $this->title = $title;
        $this->subject = $subject;
        $this->price = $price;
        $this->quantity = $quantity;
    }

    public function jsonSerialize()
    {
        return [
            'id' => $this->id,
            'title' => $this->title,
            'quantity' => $this->quantity,
            'price' => $this->price,
            'subject' => $this->subject,
        ];
    }

    public function SetPrice($price)
    {
        $this->price = $price;
    }

    public function IncrementQuantity($quantity)
    {
        $this->quantity += $quantity;
    }

    public function DecrementQuantity($quantity)
    {
        $this->quantity -= $quantity;
    }
}