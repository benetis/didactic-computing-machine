import { Component, OnInit } from '@angular/core';
import {Product} from '../classes/product';
import {ProductsService} from '../services/products.service';

@Component({
  selector: 'spa-products',
  templateUrl: 'products.component.html',
  styleUrls: ['products.component.css']
})
export class ProductsComponent implements OnInit {

  public items: Product[] = [];

  constructor(private productsServices: ProductsService) {
    this.items = productsServices.getProducts();
  }

  ngOnInit() {
  }

}
