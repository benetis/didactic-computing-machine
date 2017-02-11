import { Component, OnInit } from '@angular/core';
import {Item} from '../classes/product';
import {ProductsService} from '../services/products.service';

@Component({
  selector: 'spa-products',
  templateUrl: 'products.component.html',
  styleUrls: ['products.component.css']
})
export class ProductsComponent implements OnInit {

  public items: Item[] = [];

  constructor(private productsServices: ProductsService) {
    this.items = productsServices.getProducts();
  }

  ngOnInit() {
  }

}
