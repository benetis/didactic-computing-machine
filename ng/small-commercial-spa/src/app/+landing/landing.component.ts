import { Component, OnInit } from '@angular/core';
import {Item} from './classes/item';

@Component({
  selector: 'spa-landing',
  templateUrl: 'landing.component.html',
  styleUrls: ['landing.component.css']
})
export class LandingComponent implements OnInit {

  public items: Item[] = [
    <Item>{name: 'Blue item', price: 123.09, colors: ['blue']},
    <Item>{name: 'Green item', price: 99.09, colors: ['green']},
    <Item>{name: 'Green and gray', price: 99.09, colors: ['green', 'gray']},
    <Item>{name: 'Blue and gray', price: 99.09, colors: ['blue', 'gray']},
    <Item>{name: 'Green and blue', price: 99.09, colors: ['green', 'blue']},
    <Item>{name: 'Green and blue', price: 99.09, colors: ['green', 'blue']},
    <Item>{name: 'Gray', price: 99.09, colors: ['gray']},
  ];

  constructor() { }

  ngOnInit() {
  }

}
