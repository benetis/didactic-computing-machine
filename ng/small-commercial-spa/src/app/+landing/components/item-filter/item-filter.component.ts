import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ItemColor, ItemFilter} from '../../classes/item';

@Component({
  selector: 'spa-item-filters',
  templateUrl: './item-filter.component.html',
  styleUrls: ['./item-filter.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ItemFilterComponent implements OnInit {

  @Input() availableFilters: ItemFilter[] = [];
  @Input() activatedFilters: ItemFilter[] = [];
  @Output() activeFilters: EventEmitter<ItemFilter[]> = new EventEmitter<ItemFilter[]>();

  constructor() {
  }

  ngOnInit() {
  }

  public filterColor(color: ItemColor): string {
    const colors = {
      'blue': 'blue',
      'green': 'green',
      'gray': 'gray'
    };
    return colors[color];
  }

  public filterActive(filter: ItemFilter): boolean {
    return this.activatedFilters.find(_ => _.color === filter.color) != null;
  }

  public changeFilterStatus(filter: ItemFilter) {
    this.activeFilters.emit([filter]);
  }
}
