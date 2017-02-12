import {Injectable} from '@angular/core';
import {Product} from '../classes/product';
@Injectable()
export class CartService {
  private itemsInCart: Product[] = [];

  public addToCart(item: Product) {
    this.itemsInCart = this.itemsInCart.concat(item);
  }

  public getItems(): Product[] {
    return this.itemsInCart;
  }
}
