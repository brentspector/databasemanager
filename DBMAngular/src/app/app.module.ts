import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { DexihTableModule } from 'dexih-ngx-table';
import { DndModule } from 'ng2-dnd';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './/app-routing.module';
import { TableCreateComponent } from './table-create/table-create.component';
import { TableViewComponent } from './table-view/table-view.component';
import { HomepageComponent } from './homepage/homepage.component';
import { TableEditComponent } from './table-edit/table-edit.component';
import { AppHeaderComponent } from './app-header/app-header.component';
import { MessengerComponent } from './messenger/messenger.component';

@NgModule({
  declarations: [
    AppComponent,
    TableCreateComponent,
    TableViewComponent,
    HomepageComponent,
    TableEditComponent,
    AppHeaderComponent,
    MessengerComponent
  ],
  imports: [
    BrowserModule, DexihTableModule, DndModule.forRoot(), NgbModule.forRoot(),
    FormsModule, ReactiveFormsModule,  AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
