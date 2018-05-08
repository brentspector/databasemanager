import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';

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
      url: new FormControl('', [Validators.required, Validators.maxLength(100),
         Validators.pattern('/^https?:\/\/[\-\w]+(\.[\-\w]+)*(:[0-9]+)?\/?(\/[.\-\w]*)*$/')]),
      username: new FormControl('', [Validators.required, Validators.maxLength(25), Validators.pattern('^[\w\.]+')]),
      password: new FormControl('', [Validators.required, Validators.maxLength(25)])
    });
  }

  getDatabase() {
    console.log(this.credentialsGroup.value);
  }
}
