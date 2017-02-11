import { Component, OnInit } from '@angular/core';
import {Item} from './classes/item';

@Component({
  selector: 'spa-products',
  templateUrl: 'products.component.html',
  styleUrls: ['products.component.css']
})
export class ProductsComponent implements OnInit {

  public items: Item[] = [
    <Item>{id: 1, name: 'Blue item', price: 123.09, colors: ['blue']},
    <Item>{id: 2, name: 'Green and gray', price: 99.09, colors: ['green', 'gray']},
    <Item>{id: 3, name: 'Green item', price: 99.09, colors: ['green']},
    <Item>{id: 4, name: 'Blue and gray', price: 99.09, colors: ['blue', 'gray']},
    <Item>{id: 5, name: 'Green and blue', price: 99.09, colors: ['green', 'blue']},
    <Item>{id: 6, name: 'Green and blue', price: 99.09, colors: ['green', 'blue']},
    <Item>{id: 7, name: 'Gray', price: 99.09, colors: ['gray']},
    <Item>{id: 8, name: 'Blue', price: 99.09, colors: ['blue']},
    <Item>{id: 9, name: 'All colors', price: 99.09, colors: ['gray', 'blue', 'green']},
  ];

  constructor() { }

  ngOnInit() {
  }

}
