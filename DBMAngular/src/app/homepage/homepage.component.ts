import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { MessageService } from '../message.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  credentialsGroup: FormGroup;

  constructor(private messageService: MessageService, private router: Router) { }

  ngOnInit() {
    this.credentialsGroup = new FormGroup({
      url: new FormControl('', [Validators.required, Validators.maxLength(100),
         Validators.pattern('^(?!_\\.\\-)[\\w\\.\\-]+:[\\d]+((:[\\w\\.\\-]+)|(/[\\w\\.\\-]+))')]),
      username: new FormControl('', [Validators.required, Validators.maxLength(25), Validators.pattern('^(?!\\.|_)[\\w\\.]+')]),
      password: new FormControl('', [Validators.required, Validators.maxLength(25)])
    });
  }

  getDatabase() {
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
      console.log(this.credentialsGroup.value);
      this.router.navigateByUrl('/view');
    }
  }
}
