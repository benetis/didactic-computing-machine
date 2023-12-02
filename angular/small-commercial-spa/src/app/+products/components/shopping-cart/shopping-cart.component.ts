import {Component, OnInit} from '@angular/core';
import {CartService} from '../../../services/cart.service';
import {Product} from '../../../classes/product';
import {Observable} from 'rxjs';
import {of} from 'rxjs/observable/of';

@Component({
  selector: 'spa-shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css']
})
export class ShoppingCartComponent implements OnInit {

  public shoppingCartItems$: Observable<Product[]> = of([]);
  public shoppingCartItems: Product[] = [];

  constructor(private cartService: CartService) {
    this.shoppingCartItems$ = this
      .cartService
      .getItems();

    this.shoppingCartItems$.subscribe(_ => this.shoppingCartItems = _);
  }

  ngOnInit() {
  }

  public getTotal(): Observable<number> {
    return this.cartService.getTotalAmount();
  }

  public removeItem(item: Product) {
    this.cartService.removeFromCart(item)
  }

}
