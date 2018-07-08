import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import {NgbTabChangeEvent} from '@ng-bootstrap/ng-bootstrap';

import { TableDataService } from '../table-data.service';
import { MessageService } from '../message.service';

@Component({
  selector: 'app-table-create',
  templateUrl: './table-create.component.html',
  styleUrls: ['./table-create.component.css']
})
export class TableCreateComponent implements OnInit {

  fileType = 'Auto';
  fileResult: any;
  uploadResult = '';
  credentialsGroup: FormGroup;
  tableGroup: FormGroup;
  tableFields: any[];

  constructor(private tableDataService: TableDataService, private messageService: MessageService) { }

  ngOnInit() {
    this.fileResult = '';
    this.credentialsGroup = new FormGroup({
      url: new FormControl('', [Validators.required, Validators.maxLength(100),
         Validators.pattern('^(?!_\\.\\-)[\\w\\.\\-]+:[\\d]+((:[\\w\\.\\-]+)|(/[\\w\\.\\-]+))')]),
      username: new FormControl('', [Validators.required, Validators.maxLength(25), Validators.pattern('^(?!\\.|_)[\\w\\.]+')]),
      password: new FormControl('', [Validators.required, Validators.maxLength(25)])
    });
  }

  private setFileType(fileType: string) {
    this.fileType = fileType;
  }

  private submitFile(files: FileList) {
    this.tableDataService.postFile(files.item(0), this.fileType).subscribe(response => {
      this.fileResult = response['resultMessage'];
      this.tableGroup = this.generateFormGroup();
    });
  }

  private confirmUpload() {
    this.messageService.clear();
    let formValid = true;
    let controlError = this.credentialsGroup.get('url').errors;
    if (controlError != null) {
      Object.keys(controlError).forEach(error => {
        if (error === 'pattern') {
          this.messageService.add('DB Address must meet pattern restrictions below.', 'danger');
        } else if (error === 'maxlength') {
          this.messageService.add('DB Address cannot exceed 100 characters.', 'danger');
        } else if (error === 'required') {
          this.messageService.add('DB Address is required.', 'danger');
        } else {
          this.messageService.add('DB Address failed on ' + error, 'danger');
        }
        formValid = false;
      });
    }

    controlError = this.credentialsGroup.get('username').errors;
    if (controlError != null) {
      Object.keys(controlError).forEach(error => {
        if (error === 'pattern') {
          this.messageService.add('DB Username/Schema must meet pattern restrictions below.', 'danger');
        } else if (error === 'maxlength') {
          this.messageService.add('DB Username/Schema cannot exceed 25 characters.', 'danger');
        } else if (error === 'required') {
          this.messageService.add('DB Username/Schema is required.', 'danger');
        } else {
          this.messageService.add('DB Username/Schema failed on ' + error, 'danger');
        }
        formValid = false;
      });
    }

    controlError = this.credentialsGroup.get('password').errors;
    if (controlError != null) {
      Object.keys(controlError).forEach(error => {
        if (error === 'maxlength') {
          this.messageService.add('Password cannot exceed 25 characters.', 'danger');
        } else if (error === 'required') {
          this.messageService.add('Password is required.', 'danger');
        } else {
          this.messageService.add('Password failed on ' + error, 'danger');
        }
        formValid = false;
      });
    }

    if (formValid) {
      this.tableDataService.confirmFile().subscribe(response => this.uploadResult = response['resultMessage']);
    }
  }

  private generateFormGroup() {
    const group: any = {};

    this.tableDataService.getHeaders().subscribe(data => {
      this.tableFields = data;
      data.forEach(column => {
        group[column] = new FormControl('Choose...');
        group[column + 'var1'] = new FormControl(1);
        group[column + 'var2'] = new FormControl(0);
      });
    });

    return new FormGroup(group);
  }

  private updateFormGroup(column: string, newValue: string) {
    this.tableGroup.value[column] = newValue;
  }
}
