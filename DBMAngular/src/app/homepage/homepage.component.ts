import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  credentialsGroup: FormGroup;

  constructor() { }

  ngOnInit() {
    this.credentialsGroup = new FormGroup({
      url: new FormControl(),
      username: new FormControl(),
      password: new FormControl()
    });
  }

  getDatabase() {
    console.log(this.credentialsGroup.value);
  }

}
