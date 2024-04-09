import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrl: './product.component.css'
})
export class ProductComponent  implements OnInit{
  public products:any
  constructor(public httpClient:HttpClient) {
  }
    ngOnInit(): void {
    this.httpClient.get("http://localhost:8082/products").subscribe(
      {next:data=>this.products=data,error:err=>console.log(err)}
    )
    }

}
