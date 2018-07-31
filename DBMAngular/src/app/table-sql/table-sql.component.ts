import { Component, OnInit } from '@angular/core';
import { TableDataService } from '../table-data.service';

@Component({
  selector: 'app-table-sql',
  templateUrl: './table-sql.component.html',
  styleUrls: ['./table-sql.component.css']
})
export class TableSqlComponent implements OnInit {

  constructor(private tableDataService: TableDataService) { }

  ngOnInit() {
  }

  submitSQL(sql: string) {
    this.tableDataService.submitSQL(sql);
  }

}
