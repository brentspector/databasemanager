import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { NgbTabChangeEvent } from '@ng-bootstrap/ng-bootstrap';

import { TableDataService } from '../table-data.service';
import { TableEditComponent } from '../table-edit/table-edit.component';
import { Router } from '../../../node_modules/@angular/router';

@Component({
  selector: 'app-table-view',
  templateUrl: './table-view.component.html',
  styleUrls: ['./table-view.component.css']
})
export class TableViewComponent implements OnInit {
  @ViewChild('editModule')
  editModule: TableEditComponent;
  tableChoice: FormGroup;
  private tableList: any[];
  private tableTest = [{name: '', contents: ''}];

  constructor(private tableDataService: TableDataService, private router: Router) {
   }

  ngOnInit() {
    this.getTableList();
  }

  getTableList() {
    this.tableDataService.getList().subscribe(data => {
      this.tableList = data;
      this.tableChoice = this.generateFormGroup();
    });
  }

  getTableData() {
    this.tableDataService.getData(this.tableChoice.value).subscribe(data => {
      this.tableTest = data;
    });
  }

  generateFormGroup() {
    const group: any = {};

    this.tableList.forEach(table => {
      group[table] = new FormControl(false);
    });

    return new FormGroup(group);
  }

  deleteTable(table: Object) {
    this.tableDataService.deleteTable(table.toString()).subscribe(result => {
      this.tableTest = [{name: '', contents: ''}];
      this.tableChoice = new FormGroup({});
      this.getTableList();
    });
  }

  private toggleShow($event: NgbTabChangeEvent) {
    this.tableDataService.setShowTable($event.nextId);
  }

  private getShowTable(): string {
    return this.tableDataService.getShowTable();
  }
  private openRecordEdit(record) {
    this.editModule.openRecordEdit(record);
  }
}
