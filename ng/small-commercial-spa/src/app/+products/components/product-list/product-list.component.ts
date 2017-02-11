import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {Item, ItemFilter} from '../../../classes/product';

@Component({
  selector: 'spa-item-list',
  templateUrl: 'product-list.component.html',
  styleUrls: ['product-list.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush

})
export class ItemListComponent implements OnInit {

  @Input() public items: Item[] = [];

  public readonly filters: ItemFilter[] = [
    <ItemFilter>{color: 'blue'},
    <ItemFilter>{color: 'green'},
    <ItemFilter>{color: 'gray'},
  ];

  public activeFilters: ItemFilter[] = [];

  constructor() {
  }

  ngOnInit() {
  }

  public itemsAfterFilter(): Item[] {
    return this.items.filter((item: Item) => {
      const matchesActiveFilter: boolean = this.activeFilters.reduce((prev, curr) => {
        if (item.colors.includes(curr.color)) {
          return prev && true;
        } else {
          return false;
        }
      }, true);

      return matchesActiveFilter;
    });
  }

  public updateActivatedFilters(filters: ItemFilter[]) {
    this.activeFilters = filters;
  }
}
