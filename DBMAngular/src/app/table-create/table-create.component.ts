import { Component, OnInit } from '@angular/core';
import {NgbTabChangeEvent} from '@ng-bootstrap/ng-bootstrap';

import { TableDataService } from '../table-data.service';

@Component({
  selector: 'app-table-create',
  templateUrl: './table-create.component.html',
  styleUrls: ['./table-create.component.css']
})
export class TableCreateComponent implements OnInit {

  fileResult: any;
  uploadResult = '';

  constructor(private tableDataService: TableDataService) { }

  ngOnInit() {
    this.fileResult = '';
  }

  private submitFile(files: FileList) {
    this.tableDataService.postFile(files.item(0)).subscribe(response => this.fileResult = response['resultMessage']);
  }

  private confirmUpload() {
    this.tableDataService.confirmFile().subscribe(response => this.uploadResult = response['resultMessage']);
  }
}
