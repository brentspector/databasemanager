import { Component , OnInit} from '@angular/core';
import { Router, NavigationStart } from '@angular/router';
import { MessageService } from './message.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';

  constructor (private routes: Router, private messageService: MessageService) {}

  ngOnInit() {
    this.routes.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.messageService.clear();
      }
    });
  }
}
