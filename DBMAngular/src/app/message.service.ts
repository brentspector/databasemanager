import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  messages: Object[] = [];

  add(message: string, status) {
    this.messages.push({comment: message, result: status});
  }

  clear() {
    this.messages = [];
  }
}
