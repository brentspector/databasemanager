import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError } from 'rxjs/operators';

import { MessageService } from './message.service';

@Injectable()
export class TableDataService {
  private showTable = '0';
  private tableList = ['Test', 'Cards'];

  private tableData = [
    {
      name: 'Test',
      headers: ['ID', 'Name'],
      contents: [{'ID': 1, 'Name': 'Bob'}, {'ID': 2, 'Name': 'Donnie'}]
    },
    {
      name: 'Cards',
      headers: ['ID', 'Carte'],
      contents: [{'ID': 1, 'Carte': 'Al-lycs'}, {'ID': 2, 'Carte': 'Apollo'}, {'ID': 3, 'Carte': 'Zatman Cr'}]
    }
  ];
  private fileStatus = {'result': 'success', 'resultMessage': this.tableData};
  private uploadStatus = {'result': 'success', 'resultMessage': 'File Uploaded Successfully'};

  constructor(private messageService: MessageService) { }

  postFile(file: File) {
    // TODO: Get error message from return and submit to handle error
    return of(this.fileStatus).pipe(catchError(this.handleError('Submit table file', [])));
  }

  confirmFile() {
    // TODO: Get error message from return and submit to handle error
    return of(this.uploadStatus).pipe(catchError(this.handleError('Confirm table file', [])));
  }

  getList(): Observable<any[]> {
    return of(this.tableList).pipe(catchError(this.handleError('Fetch table list', [])));
  }

  getData(requestList: Object): Observable<any[]> {
    this.messageService.clear();
    for (const key in requestList) {
      if (requestList[key]) {
        this.report(key, 'dark');
      }
    }
    return of(this.tableData).pipe(catchError(this.handleError('Fetch table details', [])));
  }

  getHeaders(): Observable<any[]> {
    return of(this.tableData[this.showTable].headers).pipe(catchError(this.handleError('Fetch table headers', [])));
  }

  getShowTable() {
    return this.showTable;
  }

  setShowTable(table: string) {
    this.showTable = table;
  }

  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error);
      this.messageService.clear();
      this.report(`${operation} failed: ${error.message}`, 'danger');
      return of(result as T);
    };
  }

  private report(message: string, status: string) {
    this.messageService.add(message, status);
  }
}
