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

  constructor(private modalService: NgbModal, private tableDataService: TableDataService) {}

  open(content) {
    this.modalGroup = this.generateFormGroup();
    this.modalService.open(content).result.then((result) => {});
  }

  ngOnInit() {
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

  private submitNewRecord() {
    console.log(this.modalGroup.value);
  }
}
