import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './/app-routing.module';
import { TableCreateComponent } from './table-create/table-create.component';
import { TableViewComponent } from './table-view/table-view.component';
import { HomepageComponent } from './homepage/homepage.component';
import { TableEditComponent } from './table-edit/table-edit.component';
import { TableDataService } from './table-data.service';
import { DexihTableModule } from 'dexih-ngx-table';
import { AppHeaderComponent } from './app-header/app-header.component';


@NgModule({
  declarations: [
    AppComponent,
    TableCreateComponent,
    TableViewComponent,
    HomepageComponent,
    TableEditComponent,
    AppHeaderComponent
  ],
  imports: [
    BrowserModule, FormsModule, ReactiveFormsModule, NgbModule.forRoot(),
    AppRoutingModule, DexihTableModule
  ],
  providers: [TableDataService],
  bootstrap: [AppComponent]
})
export class AppModule { }
