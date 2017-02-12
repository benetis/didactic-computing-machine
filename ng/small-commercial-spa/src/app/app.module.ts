import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import {routes} from './app.routes';
import {RouterModule} from '@angular/router';
import {ProductsModule} from './+products/products.module';
import {ProductsService} from './services/products.service';
import {CartService} from './services/cart.service';
import {Location, CommonModule} from '@angular/common';


@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    CommonModule,
    BrowserModule,
    FormsModule,
    HttpModule,
    ProductsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [ProductsService, CartService, Location],
  bootstrap: [AppComponent]
})
export class AppModule { }
