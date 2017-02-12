import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ProductsService} from '../../../services/products.service';
import {Product} from '../../../classes/product';
import {CartService} from '../../../services/cart.service';

@Component({
  selector: 'spa-product-details',
  templateUrl: 'product-details.component.html',
  styleUrls: ['product-details.component.css']
})
export class ProductDetailsComponent implements OnInit {
  public product: Product = {};

  constructor(private route: ActivatedRoute
    , private productsService: ProductsService
    , private cartService: CartService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      this.product = this.productsService.getProduct(id);
    });
  }

  public addToCart(product: Product) {
    this.cartService.addToCart(product);
  }
}
