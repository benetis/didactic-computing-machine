import { Component, OnInit } from '@angular/core';
import {Item} from './classes/item';

@Component({
  selector: 'spa-landing',
  templateUrl: 'landing.component.html',
  styleUrls: ['landing.component.css']
})
export class LandingComponent implements OnInit {

  public items: Item[] = [
    <Item>{name: 'Some item1', price: 123.09},
    <Item>{name: 'Name of item', price: 99.09},
  ];

  constructor() { }

  ngOnInit() {
  }

}
