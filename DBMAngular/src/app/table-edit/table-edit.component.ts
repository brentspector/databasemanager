import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { TableDataService } from '../table-data.service';

@Component({
  selector: 'app-table-edit',
  templateUrl: './table-edit.component.html',
  styleUrls: ['./table-edit.component.css']
})
export class TableEditComponent implements OnInit {

  modalGroup: FormGroup;
  modalFields: any[];
  columnGroup: FormGroup;

  constructor(private modalService: NgbModal, private tableDataService: TableDataService) {}

  ngOnInit() {
    this.columnGroup = new FormGroup({
      columnName: new FormControl(),
      columnType: new FormControl('Choose...'),
      columnTypevar1: new FormControl(''),
      columnTypevar2: new FormControl('')
   });
  }

  open(content) {
    this.modalGroup = this.generateFormGroup();
    this.modalService.open(content).result.then((result) => {});
  }

  generateFormGroup() {
    const group: any = {};

    this.tableDataService.getHeaders().subscribe(data => {
      this.modalFields = data;
      data.forEach(column => {
        group[column] = new FormControl('');
      });
    });

    return new FormGroup(group);
  }

    private updateFormGroup(column: string, newValue: string) {
      this.columnGroup.value[column] = newValue;
  }

  private submitNewRecord() {
    this.tableDataService.addNewRecord(this.modalGroup.value);
  }

  private submitFile(files: FileList, uploadType: string) {
    this.tableDataService.addMassNewRecords(files, uploadType);
  }

  private submitNewColumn() {
    this.tableDataService.addNewColumn(this.columnGroup.value);
  }
}
