import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';

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
  constructor() { }

  getList(): Observable<any[]> {
    return of(this.tableList);
  }

  getData(requestList: Object): Observable<any[]> {
    for (const key in requestList) {
      if (requestList[key]) {
        console.log(key);
      }
    }
    return of(this.tableData);
  }

  getHeaders(): Observable<any[]> {
    return of(this.tableData[this.showTable].headers);
  }

  getShowTable() {
    return this.showTable;
  }

  setShowTable(table: string) {
    this.showTable = table;
  }
}
