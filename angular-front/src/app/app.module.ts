import {APP_INITIALIZER, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CustomerComponent } from './customers/customer/customer.component';
import { ProductComponent } from './products/product/product.component';
import {HttpClientModule} from "@angular/common/http";
import {KeycloakAngularModule, KeycloakService} from "keycloak-angular";
console.log("window.location.origin:::"+window.location.origin)
function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'realm1',
        clientId: 'angualr_client'
      },
      //,loadUserProfileAtStartUp:true,
      initOptions: {

         onLoad: 'check-sso',
        //onLoad: 'login-required',
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
        //redirectUri:window.location.origin+ + '/assets/silent-check-sso.html',
       //checkLoginIframe: false,
       // pkceMethod: "S256"
      }
    });
}

@NgModule({
  declarations: [
    AppComponent,
    CustomerComponent,
    ProductComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    KeycloakAngularModule
  ],
  providers: [
    {provide: APP_INITIALIZER, useFactory: initializeKeycloak, multi: true,deps:[KeycloakService]}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
