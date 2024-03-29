<?php

/** @var \Laravel\Lumen\Routing\Router $router */

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It is a breeze. Simply tell Lumen the URIs it should respond to
| and give it the Closure to call when that URI is requested.
|
*/

$router->get('/', function () use ($router) {
    return $router->app->version();
});


$router->get('/search/{subject}', 'CatalogController@getBooksBySubject');
$router->get('/book/{id}', 'CatalogController@getBookById');
$router->put('/book/{id}', 'CatalogController@updateBookById');
$router->put('/purchase/{id}', 'CatalogController@purchaseBookById');
$router->post('/notify', 'CatalogController@receiveNotify');