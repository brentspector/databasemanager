import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomepageComponent } from './homepage/homepage.component';
import { TableCreateComponent } from './table-create/table-create.component';
import { TableViewComponent } from './table-view/table-view.component';

const routes: Routes = [
  { path: '', component: HomepageComponent },
  { path: 'upload', component: TableCreateComponent },
  { path: 'view', component: TableViewComponent },
  { path: '**', redirectTo: '', pathMatch: 'full' }
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
