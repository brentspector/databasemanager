import { Component, OnInit, ViewChild, ElementRef, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { TableDataService } from '../table-data.service';

@Component({
  selector: 'app-table-edit',
  templateUrl: './table-edit.component.html',
  styleUrls: ['./table-edit.component.css']
})
export class TableEditComponent implements OnInit {

  @Output()
  refresh: EventEmitter<any> = new EventEmitter();
  @ViewChild('editRecord')
  editRecord: ElementRef;
  modalRef: NgbModalRef;
  modalGroup: FormGroup;
  modalFields: any[];
  tableNames: any[];
  columnGroup: FormGroup;
  columnModifyGroup: FormGroup;
  fileChoice: string;
  tableChoice: string;

  constructor(private modalService: NgbModal, private tableDataService: TableDataService) {}

  ngOnInit() {
    this.columnGroup = new FormGroup({
      columnName: new FormControl(),
      columnType: new FormControl('Choose...'),
      columnTypevar1: new FormControl(''),
      columnTypevar2: new FormControl('')
   });
   this.columnModifyGroup = new FormGroup({
     columnName: new FormControl('Select...'),
     columnData: new FormControl(''),
     columnAction: new FormControl()
   });
   this.tableDataService.getList().subscribe(data => {
     this.tableNames = data;
  });
   this.fileChoice = 'Select...';
   this.tableChoice = 'Select...';
  }

  private open(content) {
    this.modalGroup = this.generateFormGroup();
    this.modalRef = this.modalService.open(content);
  }

  private generateFormGroup() {
    const group: any = {};

    this.tableDataService.getHeaders().subscribe(data => {
      this.modalFields = data;
      data.forEach(column => {
        group[column] = new FormControl('');
      });
    });
    group['primaryKey'] = new FormControl('');
    group['deleteCheck'] = new FormControl(false);
    return new FormGroup(group);
  }

    private updateFormGroup(column: string, newValue: string) {
      this.columnGroup.value[column] = newValue;
  }

  private submitNewRecord() {
    this.tableDataService.addNewRecord(this.modalGroup.value);
    this.modalRef.close();
  }

  private submitFile(files: FileList) {
    this.tableDataService.massModifyRecords(files, this.fileChoice);
    this.modalRef.close();
  }

  private submitNewColumn() {
    this.tableDataService.addNewColumn(this.columnGroup.value);
    this.modalRef.close();
  }

  openRecordEdit(record) {
    this.modalGroup = this.generateFormGroup();
    this.modalGroup.patchValue(record);
    this.modalRef = this.modalService.open(this.editRecord);
  }

  private updatePrimaryKey(columnChoice) {
    this.modalGroup.value['primaryKey'] = columnChoice;
  }

  private submitEditRecord() {
    this.tableDataService.updateRecord(this.modalGroup.value);
    this.modalRef.close();
  }

  private updateColumnChoice(columnChoice) {
    this.columnModifyGroup.value['columnName'] = columnChoice;
  }

  private updateFileChoice(fileChoice) {
    this.fileChoice = fileChoice;
  }

  private updateTableChoice(tableChoice) {
    this.tableChoice = tableChoice;
  }

  private submitModifyColumnData() {
    this.tableDataService.modifyColumnData(this.columnModifyGroup.value);
    this.modalRef.close();
  }

  private submitTableDeletion() {
    this.refresh.emit(this.tableChoice);
    this.modalRef.close();
  }
}
