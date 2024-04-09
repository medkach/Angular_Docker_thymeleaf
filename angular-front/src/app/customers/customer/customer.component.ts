import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.css'
})
export class CustomerComponent implements OnInit{
  public customers:any
  constructor(public httpClient:HttpClient) {
  }
    ngOnInit(): void {
    this.httpClient.get("http://localhost:8081/customers").subscribe(
      {next:data =>this.customers=data,error: err => console.log(err)}
    );
    }

}
