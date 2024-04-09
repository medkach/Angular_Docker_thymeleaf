import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {CustomerComponent} from "./customers/customer/customer.component";
import {ProductComponent} from "./products/product/product.component";
import {authGuard} from "./guards/auth.guard";

const routes: Routes = [
  {path:"customers",component:CustomerComponent},
  {path:"products",component:ProductComponent,canActivate:[authGuard],data:{roles:['admin']}}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
