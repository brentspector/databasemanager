import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TableSqlComponent } from './table-sql.component';

describe('TableSqlComponent', () => {
  let component: TableSqlComponent;
  let fixture: ComponentFixture<TableSqlComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TableSqlComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TableSqlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
