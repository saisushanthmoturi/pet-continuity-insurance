# Frontend Project: 
Your goal:
generate the FE from my project,
currently there is a base/default project at CLIENT_CODE_LOCATION
read the below details, rules, specfication, etc, and implement the angular frontend project.
- this project is developed in local only, and the demo will also be in local, only keep the api calls from the localhost.
- dont make everything complex, keep it simple and functional.
- use tailwind-css
- also refer the FLOWCHARTS to understand the architecture of policy and claims
- refer the footer image i attached for color only, keep our footer simple,  only whats needed. 

## UI, themes, images, etc..
read the main plan that has the the user journey mapping to create a UI
color options in the color theme:
1. black : #000000
2. Claret : #75013F
3. Fuchsia : #FE3082
4. Warm grey : #EAE5DF
5. White: #FFFFFF
refer the hartford image i have attached to see how these colors are used. -> use it only to understand where to use which color dont try to exactly implement all component it in.
cause our project does only 1 insurance

implement this navbar  -> horizontal navbar, with clean slightly rounded edges -> that doesn't span entire width, only 70% of the width in the center. should looks simple and clean

## Folder structure
in the angular project use this folder structure, use ng generate for them if possible.
refer this folder struture to understand how to breakdown the components, where to store states, models, etc..:
this is from a different project for reference.
```bash
    E:\079_data\develop\hartford-myspace\hartford-training\miniprojects\ecommerce-main\ecommerce-client\src\app>tree /f
    Folder PATH listing for volume New Volume
    Volume serial number is D843-C2C1
    E:.
    │   app.config.ts
    │   app.css
    │   app.html
    │   app.routes.ts
    │   app.spec.ts
    │   app.ts
    │   app2.html
    │   
    ├───features
    │   ├───auth
    │   │   ├───components
    │   │   │   └───login-component
    │   │   │           login-component.css
    │   │   │           login-component.html
    │   │   │           login-component.spec.ts
    │   │   │           login-component.ts
    │   │   │           
    │   │   ├───models
    │   │   │       JwtReqDTO.ts
    │   │   │       JwtResDTO.ts
    │   │   │       
    │   │   ├───service
    │   │   │       auth-service.spec.ts
    │   │   │       auth-service.ts
    │   │   │       
    │   │   └───state
    │   │           login.action.ts
    │   │           login.effects.ts
    │   │           login.reducer.ts
    │   │           login.selector.ts
    │   │           login.state.ts
    │   │           
    │   ├───common
    │   │   └───components
    │   │       ├───footer-component
    │   │       │       footer-component.css
    │   │       │       footer-component.html
    │   │       │       footer-component.spec.ts
    │   │       │       footer-component.ts
    │   │       │       
    │   │       └───header-component
    │   │               header-component.css
    │   │               header-component.html
    │   │               header-component.spec.ts
    │   │               header-component.ts
    │   │               
    │   ├───orders
    │   │   ├───components
    │   │   ├───model
    │   │   │       orderDTO.ts
    │   │   │       orderItemDTO.ts
    │   │   │       
    │   │   ├───services
    │   │   │       orders-service.spec.ts
    │   │   │       orders-service.ts
    │   │   │       
    │   │   └───state
    │   │           orders.actions.ts
    │   │           orders.effects.ts
    │   │           orders.reducer.ts
    │   │           orders.selector.ts
    │   │           orders.state.ts
    │   │           
    │   ├───products
    │   │   ├───components
    │   │   │   ├───add-product-component
    │   │   │   │       add-product-component.css
    │   │   │   │       add-product-component.html
    │   │   │   │       add-product-component.spec.ts
    │   │   │   │       add-product-component.ts
    │   │   │   │       
    │   │   │   ├───edit-product-component
    │   │   │   │       edit-product-component.css
    │   │   │   │       edit-product-component.html
    │   │   │   │       edit-product-component.spec.ts
    │   │   │   │       edit-product-component.ts
    │   │   │   │       
    │   │   │   ├───product-component
    │   │   │   │       product-component.css
    │   │   │   │       product-component.html
    │   │   │   │       product-component.spec.ts
    │   │   │   │       product-component.ts
    │   │   │   │       
    │   │   │   └───show-products-component
    │   │   │           show-products-component.css
    │   │   │           show-products-component.html
    │   │   │           show-products-component.spec.ts
    │   │   │           show-products-component.ts
    │   │   │           
    │   │   ├───models
    │   │   │       productDTO.ts
    │   │   │       
    │   │   ├───services
    │   │   │       product-service.spec.ts
    │   │   │       product-service.ts
    │   │   │       
    │   │   └───state
    │   │           products.actions.ts
    │   │           products.effects.ts
    │   │           products.reducer.ts
    │   │           products.selectors.ts
    │   │           products.state.ts
    │   │           
    │   └───user
    │       ├───components
    │       │   ├───admin-component
    │       │   │       admin-component.css
    │       │   │       admin-component.html
    │       │   │       admin-component.spec.ts
    │       │   │       admin-component.ts
    │       │   │       
    │       │   ├───landing-component
    │       │   │       landing-component.css
    │       │   │       landing-component.html
    │       │   │       landing-component.spec.ts
    │       │   │       landing-component.ts
    │       │   │       
    │       │   └───users-components
    │       │           users-components.css
    │       │           users-components.html
    │       │           users-components.spec.ts
    │       │           users-components.ts
    │       │           
    │       ├───models
    │       │       UserReqDTO.ts
    │       │       UserResDTO.ts
    │       │       
    │       ├───services
    │       │       user-service.spec.ts
    │       │       user-service.ts
    │       │       
    │       └───state
    │               user.actions.ts
    │               user.effect.ts
    │               user.reducer.ts
    │               user.selector.ts
    │               user.state.ts
    │               
    ├───guards
    │       authGuard.ts
    │       editGuard.ts
    │       roleGuard.ts
    │       
    └───interceptors
            httpReqInterceptor.ts
        
```

## gaurds in codebase: it should have a authgaurd
```ts
    import { inject } from "@angular/core";
    import { CanActivateFn, Router } from "@angular/router";

    export const authGuard:CanActivateFn=()=>{
        let router=inject(Router)
        if( localStorage.getItem('currentUser')==null){
            router.navigate(['auth/login'])
            return false;
        }
        return true;
    }
```


## intercpetors code: it should have authInterceptor

```ts
    import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
    import { Router } from '@angular/router';
    import { inject } from '@angular/core';
    import { catchError, throwError } from 'rxjs';

    export const authInterceptor: HttpInterceptorFn =
    (req, next) => {

        const router = inject(Router);

        return next(req).pipe(
            catchError((error: HttpErrorResponse) => {

                if (error.status === 401) {

                    localStorage.removeItem('currentUser');

                    router.navigate(['/login']);
                }

                return throwError(() => error);
            })
        );
    };
```


## what should be in .service.ts
- use https obeservables
- use rxjs Observables
example from reference:
```ts
import { inject, Injectable, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import OrderDTO from '../model/orderDTO';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class OrdersService{

  http:HttpClient=inject(HttpClient);
  apiUrl="http://localhost:8080"
  getCart(orderId:number):Observable<OrderDTO>{
    return this.http.get<OrderDTO>(`${this.apiUrl}/user/order/${orderId}`);
  }

  placeOrder(userId:number):Observable<OrderDTO>{
    return this.http.post<OrderDTO>(`${this.apiUrl}/user/order/${userId}`,null);
  }

  addToCart(userId:number,productId:number):Observable<OrderDTO>{
    return this.http.post<OrderDTO>(`${this.apiUrl}/user/order/${userId}/${productId}`,null)
  }

  getOrders(userId:number):Observable<OrderDTO[]>{
    return this.http.get<OrderDTO[]>(`${this.apiUrl}/users/${userId}`);
  }
}


```

## ngrx implementaion
use ngrx for storing the state:
this is the details of the implementation of ngrx:

# NgRx State Management Architecture & Implementation Guide

> **E-Commerce Monolith Client (`/client`)**  
> **Tech Stack:** Angular 21 (Standalone Components) & NgRx 21 (`@ngrx/store`, `@ngrx/effects`)  
> **Pattern:** Modular 5-File Feature Slice Architecture (`state`, `actions`, `reducer`, `effects`, `selectors`)

---

## 📑 Table of Contents
1. [Architectural Overview & Core Patterns](#1-architectural-overview--core-patterns)
2. [Master File Inventory](#2-master-file-inventory)
3. [Global Store Registration (`app.config.ts`)](#3-global-store-registration-appconfigts)
4. [Deep Dive: Feature State Slices](#4-deep-dive-feature-state-slices)
   - [4.1 Auth Feature Slice (`features/auth/state`)](#41-auth-feature-slice-featuresauthstate)
   - [4.2 Orders Feature Slice (`features/orders/state`)](#42-orders-feature-slice-featuresordersstate)
   - [4.3 Products Feature Slice (`features/products/state`)](#43-products-feature-slice-featuresproductsstate)
   - [4.4 User Feature Slice (`features/user/state`)](#44-user-feature-slice-featuresuserstate)
5. [Cross-Domain Coordination & Workflow Bridges](#5-cross-domain-coordination--workflow-bridges)
6. [Store Consumption Patterns](#6-store-consumption-patterns)
   - [Route Guards with NgRx Selectors](#61-route-guards-with-ngrx-selectors)
   - [Standalone Components with Async Pipe & Dispatch](#62-standalone-components-with-async-pipe--dispatch)
7. [Conventions, Codebase Nuances & Best Practice Review](#7-conventions-codebase-nuances--best-practice-review)

---

## 1. Architectural Overview & Core Patterns

The Angular client utilizes a **decoupled, modular NgRx architecture**. Rather than having a single monolithic store file, each business domain maintains its own isolated state slice inside `src/app/features/<feature>/state/`.


src/app/features/<feature-name>/
├── models/                     <-- DTOs & Domain interfaces
├── services/                   <-- Injectable HttpClient API services
├── components/                 <-- Standalone presentation/container components
└── state/                      <-- NgRx 5-File Architecture
    ├── <feature>.state.ts      <-- (1) State interface & Initial state
    ├── <feature>.actions.ts    <-- (2) Action creators (Triggers, Success, Failure)
    ├── <feature>.reducer.ts    <-- (3) Pure reducer function & state transitions
    ├── <feature>.effects.ts    <-- (4) Side-effects, API coordination, routing
    └── <feature>.selector.ts   <-- (5) Memoized slice and property selectors


### The 4 Foundation Design Principles

#### A. Universal Asynchronous `Status` Tracking
Async operations in all features avoid ad-hoc boolean flags by adopting the standard `Status` interface defined in `login.state.ts` and reused across all domain slices:

  ```typescript
    export interface Status {
      loading: boolean;
      success: boolean;
      error: string;
    }
  ```


#### B. The Action Triad Pattern
Every remote asynchronous flow follows a predictable three-phase lifecycle:
1. **Trigger Action:** `[<DOMAIN>] <VERB>` — initiates the operation and updates status `loading: true`.
2. **Success Action:** `[<DOMAIN>] <VERB> SUCCESS` — receives the response payload, sets `success: true, loading: false`, updates domain state.
3. **Failure Action:** `[<DOMAIN>] <VERB> FAILURE` — sets `loading: false, success: false`, stores the error message.

#### C. Inner-Stream Error Isolation in Effects
All NgRx effects isolate their error handling inside inner RxJS streams (within `switchMap`), using `catchError(() => of(failureAction({ error })))`. This guarantees that an HTTP failure never terminates the outer `actions$` stream, keeping the listener active for future user dispatches.

#### D. Standalone Angular 21 Integration
State and effects are provided globally at application bootstrap in `app.config.ts` via `provideStore({...})` and `provideEffects([...])`. Components use Angular's modern `inject(Store)` function alongside the `AsyncPipe` and `@let` template variables.

---

## 2. Master File Inventory

Here is the complete catalog of all state management files, supporting DTOs, and services across the application:

| Feature / Domain | File Type | File Path (relative to `client/src/app/`) | Primary Responsibility |
| :--- | :--- | :--- | :--- |
| **Global Setup** | Config | `app.config.ts` | Configures `provideStore` & `provideEffects` |
| **Auth** | Model | `features/auth/models/JwtReqDTO.ts` | Login credentials contract (`username`, `password`) |
| **Auth** | Model | `features/auth/models/JwtResDTO.ts` | Authentication response contract (`token`, `role`) |
| **Auth** | Service | `features/auth/service/auth-service.ts` | HTTP client for `/public/auth` and `/signin` |
| **Auth** | State | `features/auth/state/login.state.ts` | Defines `Status`, `LoginState`, and `InitialLoginState` |
| **Auth** | Actions | `features/auth/state/login.action.ts` | Action creators for login, signup, logout |
| **Auth** | Reducer | `features/auth/state/login.reducer.ts` | Handles auth tokens, local storage, login/signup states |
| **Auth** | Effects | `features/auth/state/login.effects.ts` | Authenticates via HTTP, manages routing, dispatches `eraseUser` |
| **Auth** | Selectors | `features/auth/state/login.selector.ts` | Selectors for `isLoggedIn`, `loginStatus`, `signupstatus` |
| **Orders** | Model | `features/orders/model/orderDTO.ts` | Order entity contract (`id`, `items`, `placedOn`, `total`) |
| **Orders** | Model | `features/orders/model/orderItemDTO.ts` | Order line item contract (`product`, `quantity`) |
| **Orders** | Service | `features/orders/services/orders-service.ts` | HTTP client for cart, order placement, order history |
| **Orders** | State | `features/orders/state/orders.state.ts` | Defines `OrdersState` and `InitialOrdersState` |
| **Orders** | Actions | `features/orders/state/orders.actions.ts` | Actions for `getOrders`, `loadCart`, `addToCart`, `placeOrder` |
| **Orders** | Reducer | `features/orders/state/orders.reducer.ts` | Mutates cart, orders history, active product tracking |
| **Orders** | Effects | `features/orders/state/orders.effects.ts` | Executes order API calls, validates `userId`, auto-resets status |
| **Orders** | Selectors | `features/orders/state/orders.selector.ts` | Selectors for `orders`, `cart`, `productsToAdd`, statuses |
| **Products** | Model | `features/products/models/productDTO.ts` | Product entity contract (`id`, `name`, `category`, `price`, `imgUrl`) |
| **Products** | Service | `features/products/services/product-service.ts` | HTTP client for public catalog and admin CRUD |
| **Products** | State | `features/products/state/products.state.ts` | Defines `ProductsState` and `InitialProductsState` |
| **Products** | Actions | `features/products/state/products.actions.ts` | Actions for `getProducts`, `addProduct`, `editProduct`, `initiateEdit` |
| **Products** | Reducer | `features/products/state/products.reducer.ts` | Manages catalog list, editing buffer, and statuses |
| **Products** | Effects | `features/products/state/products.effects.ts` | Fetches, adds, updates products and navigates on success |
| **Products** | Selectors | `features/products/state/products.selectors.ts` | Selectors for catalog, current product under edit, statuses |
| **User** | Model | `features/user/models/UserReqDTO.ts` | User registration payload |
| **User** | Model | `features/user/models/UserResDTO.ts` | Current user identity payload (`id`, `userName`, `role`, `cart`) |
| **User** | Service | `features/user/services/user-service.ts` | HTTP client for `/user/whoami` |
| **User** | State | `features/user/state/user.state.ts` | Defines `UserState` and `InitialUserState` |
| **User** | Actions | `features/user/state/user.actions.ts` | Actions for `loadUser`, `loadUserSuccess`, `eraseUser` |
| **User** | Reducer | `features/user/state/user.reducer.ts` | Stores `currentUser`, resets on `eraseUser` |
| **User** | Effects | `features/user/state/user.effect.ts` | Loads user identity, auto-triggers `logout` on token expiration |
| **User** | Selectors | `features/user/state/user.selector.ts` | Selectors for `currentUser` and `userLoadingStatus` |
| **Guards** | Route Guard | `guards/editGuard.ts` | Uses `productToEditSelector` to guard `/products/edit` route |

---

## 3. Global Store Registration (`app.config.ts`)

In Angular 21's standalone application model, the root store is declared in `src/app/app.config.ts` using `provideStore()` and `provideEffects()`:

```typescript
// src/app/app.config.ts
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';

import { routes } from './app.routes';
import { httpReqInterceptor } from './interceptors/httpReqInterceptor';

// Reducers
import { loginReducer } from './features/auth/state/login.reducer';
import { ProductsReducer } from './features/products/state/products.reducer';
import { UserReducer } from './features/user/state/user.reducer';
import { OrdersReducer } from './features/orders/state/orders.reducer';

// Effects
import { LoginEffects } from './features/auth/state/login.effects';
import { ProductsEffects } from './features/products/state/products.effects';
import { userEffects } from './features/user/state/user.effect';
import { OrdersEffects } from './features/orders/state/orders.effects';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([httpReqInterceptor])),

    // NgRx Store Feature Reducers mapping
    provideStore({
      login: loginReducer,
      products: ProductsReducer,
      user: UserReducer,
      orders: OrdersReducer
    }),

    // NgRx Effects Registration
    provideEffects([
      LoginEffects,
      ProductsEffects,
      userEffects,
      OrdersEffects
    ])
  ]
};
```

---

## 4. Deep Dive: Feature State Slices

---

### 4.1 Auth Feature Slice (`features/auth/state`)

The Auth slice manages authentication tokens, user login status, signup status, and session termination. It also establishes the core `Status` interface used application-wide.

#### File 1: `login.state.ts`
- **Location:** `src/app/features/auth/state/login.state.ts`
- **Purpose:** Defines the shared `Status` type, the shape of `LoginState`, and initial state values.

```typescript
export interface Status {
  loading: boolean;
  success: boolean;
  error: string;
}

export interface LoginState {
  isLoggedIn: boolean;
  loginStatus: Status;
  signupstatus: Status;
}

export const InitialLoginState: LoginState = {
  isLoggedIn: false,
  loginStatus: { loading: false, success: false, error: '' },
  signupstatus: { loading: false, success: false, error: '' }
};
```

#### File 2: `login.action.ts`
- **Location:** `src/app/features/auth/state/login.action.ts`
- **Purpose:** Action creators for the login lifecycle, logout, and signup lifecycle.

```typescript
import { createAction } from "@ngrx/store";

export const login = createAction('[LOGIN] LOGIN', (jwtReq) => ({ jwtReq }));
export const loginSuccess = createAction('[LOGIN] LOGIN SUCCESS', (jwtRes) => ({ jwtRes }));
export const loginFailure = createAction('[LOGIN] LOGIN FAILURE', (error) => ({ error }));
export const logout = createAction('[LOGIN] LOGOUT');

export const signup = createAction('[SIGN UP]', (userReq) => ({ userReq }));
export const signupSuccess = createAction('[SIGN UP] SUCCESS');
export const signupFailure = createAction('[SIGN UP] FAILURE', (error) => ({ error }));
```

#### File 3: `login.reducer.ts`
- **Location:** `src/app/features/auth/state/login.reducer.ts`
- **Purpose:** Pure reducer handling immutable state updates and persisting session tokens (`token`, `role`) into `localStorage`.

```typescript
import { createReducer, on } from "@ngrx/store";
import { InitialLoginState } from "./login.state";
import * as LoginActions from "./login.action";

export const loginReducer = createReducer(
  InitialLoginState,
  on(LoginActions.login, (state, { jwtReq }) => ({
    ...state,
    loginStatus: { loading: true, success: false, error: '' }
  })),
  on(LoginActions.loginSuccess, (state, { jwtRes }) => {
    if (localStorage.getItem('token') == null) {
      localStorage.setItem('token', jwtRes.token);
      localStorage.setItem('role', jwtRes.role);
    }
    return {
      ...state,
      isLoggedIn: true,
      loginStatus: { loading: false, success: true, error: '' }
    };
  }),
  on(LoginActions.loginFailure, (state, { error }) => {
    console.log(error.error);
    return {
      ...state,
      loginStatus: { loading: false, success: false, error: error.error }
    };
  }),
  on(LoginActions.logout, (state) => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    return {
      ...state,
      isLoggedIn: false,
      loginStatus: { loading: false, success: false, error: '' }
    };
  }),
  on(LoginActions.signup, (state, { userReq }) => ({
    ...state,
    signupstatus: { loading: true, success: false, error: '' }
  })),
  on(LoginActions.signupSuccess, (state) => ({
    ...state,
    signupstatus: { loading: false, success: true, error: '' }
  })),
  on(LoginActions.signupFailure, (state, { error }) => ({
    ...state,
    signupstatus: { loading: false, success: false, error: error }
  }))
);
```

#### File 4: `login.effects.ts`
- **Location:** `src/app/features/auth/state/login.effects.ts`
- **Purpose:** Listens to `login`, `signup`, and `logout` actions, delegates to `AuthService`, handles router redirects, and dispatches cross-domain cleanups (`eraseUser`).

```typescript
import { inject, Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { AuthService } from "../service/auth-service";
import { login, loginFailure, loginSuccess, logout, signup, signupFailure } from "./login.action";
import { catchError, map, of, switchMap, tap } from "rxjs";
import { Router } from "@angular/router";
import { HttpErrorResponse } from "@angular/common/http";
import { Store } from "@ngrx/store";
import { eraseUser } from "../../user/state/user.actions";

@Injectable()
export class LoginEffects {
  private actions$ = inject(Actions);
  private authService = inject(AuthService);
  private router: Router = inject(Router);
  private state: Store = inject(Store);

  // Authenticate user, navigate to home (""), dispatch loginSuccess
  private auth$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(login),
      switchMap(({ jwtReq }) => {
        return this.authService.login(jwtReq).pipe(
          map((jwtRes) => {
            this.router.navigate([""]);
            return loginSuccess(jwtRes);
          }),
          catchError((error: HttpErrorResponse) => {
            return of(loginFailure({ error: error.error }));
          })
        );
      })
    );
  });

  // Register user, redirect to login page without dispatching further store actions
  private signin$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(signup),
      switchMap(({ userReq }) => {
        return this.authService.signup(userReq).pipe(
          tap(() => this.router.navigate(['login'])),
          catchError((error) => of(signupFailure({ error })))
        );
      })
    );
  }, { dispatch: false });

  // On logout, navigate to login page and trigger cross-domain user state reset
  private logout$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(logout),
      map(() => {
        this.router.navigate(["login"]);
        return eraseUser();
      })
    );
  });
}
```

#### File 5: `login.selector.ts`
- **Location:** `src/app/features/auth/state/login.selector.ts`
- **Purpose:** Memoized selectors for retrieving auth state values.

```typescript
import { createFeatureSelector, createSelector } from "@ngrx/store";
import { LoginState } from "./login.state";

export const loginState = createFeatureSelector<LoginState>('login');
export const isLoggedInSelector = createSelector(loginState, (state) => state.isLoggedIn);
export const loginStatusSelector = createSelector(loginState, (state) => state.loginStatus);
export const signupStatusSeletor = createSelector(loginState, (state) => state.signupstatus);
```

---

### 4.2 Orders Feature Slice (`features/orders/state`)

The Orders slice manages cart items, cart loading, adding items to cart (with item-level pending indicators), order history retrieval, and checkout / order placement.

#### File 1: `orders.state.ts`
- **Location:** `src/app/features/orders/state/orders.state.ts`
- **Purpose:** State interface with full lifecycle tracking and active product ID queue.

```typescript
import { Status } from "../../auth/state/login.state";
import OrderDTO from "../model/orderDTO";

export default interface OrdersState {
  orders: OrderDTO[];
  getOrdersStatus: Status;
  cart: OrderDTO;
  loadCartStatus: Status;
  addToCartStatus: Status;
  placeOrderStatus: Status;
  productsToAdd: number[]; // Queue of product IDs currently in flight for add-to-cart
}

export const InitialOrdersState: OrdersState = {
  orders: [],
  cart: {
    items: [],
    placedOn: undefined,
    total: 0
  },
  productsToAdd: [],
  loadCartStatus: { loading: false, success: false, error: '' },
  getOrdersStatus: { loading: false, success: false, error: '' },
  addToCartStatus: { loading: false, success: false, error: '' },
  placeOrderStatus: { loading: false, success: false, error: '' }
};
```

#### File 2: `orders.actions.ts`
- **Location:** `src/app/features/orders/state/orders.actions.ts`
- **Purpose:** Action triad creators for Orders, Cart, Add to Cart, Place Order, and status reset.

```typescript
import { createAction } from "@ngrx/store";

// Get Order History
export const getOrders = createAction('[ORDERS] GET ORDERS', (userId) => ({ userId }));
export const getOrdersSuccess = createAction('[ORDERS] GET ORDERS SUCCESS', (orders) => ({ orders }));
export const getOrdersFailure = createAction('[ORDERS] GET ORDERS FAILURE', (error) => ({ error }));

// Load Active Cart
export const loadCart = createAction('[CART] LOAD CART', (userId) => ({ userId }));
export const loadCartSuccess = createAction('[CART] LOAD CART SUCCESS', (cart) => ({ cart }));
export const loadCartFailure = createAction('[CART] LOAD CART FAILURE', (error) => ({ error }));

// Add Product to Cart
export const addToCart = createAction('[CART] ADD TO CART', (userId, productId) => ({ userId, productId }));
export const addToCartSucess = createAction('[CART] ADD TO CART SUCSESS', (cart, productId) => ({ cart, productId }));
export const addToCartFailure = createAction('[CART] ADD TO CART FAILURE', (error) => ({ error }));

// Place / Checkout Order
export const placeOrder = createAction('[CART] PLACE ORDER', (userId) => ({ userId }));
export const placeOrderSuccess = createAction('[CART] PLACE ORDER SUCCESS', (cart) => ({ cart }));
export const placeOrderFailure = createAction('[CART] PLACE ORDER FAILURE', (error) => ({ error }));
export const restPlaceOrderSuccess = createAction('[CART] REST SUCCESS STATUS');
```

#### File 3: `orders.reducer.ts`
- **Location:** `src/app/features/orders/state/orders.reducer.ts`
- **Purpose:** Manages orders state, appends new orders, clears the cart on successful checkout, and tracks pending `productsToAdd`.

```typescript
import { createReducer, on } from "@ngrx/store";
import { InitialOrdersState } from "./orders.state";
import * as OrdersActions from "./orders.actions";

export const OrdersReducer = createReducer(
  InitialOrdersState,

  // Get Orders
  on(OrdersActions.getOrders, (state) => ({
    ...state,
    getOrdersStatus: { loading: true, success: false, error: '' }
  })),
  on(OrdersActions.getOrdersSuccess, (state, { orders }) => ({
    ...state,
    orders: orders,
    getOrdersStatus: { loading: false, success: true, error: '' }
  })),
  on(OrdersActions.getOrdersFailure, (state, { error }) => ({
    ...state,
    getOrdersStatus: { loading: false, success: false, error: error }
  })),

  // Add to Cart (tracks in-flight productId)
  on(OrdersActions.addToCart, (state, { userId, productId }) => ({
    ...state,
    addToCartStatus: { loading: true, success: false, error: '' },
    productsToAdd: [...state.productsToAdd, productId]
  })),
  on(OrdersActions.addToCartSucess, (state, { cart, productId }) => ({
    ...state,
    addToCartStatus: { loading: false, success: true, error: '' },
    cart: cart,
    productsToAdd: state.productsToAdd.filter((p) => p !== productId)
  })),
  on(OrdersActions.addToCartFailure, (state, { error }) => ({
    ...state,
    addToCartStatus: { loading: false, success: false, error: error }
  })),

  // Place Order (moves cart to orders array and clears cart)
  on(OrdersActions.placeOrder, (state) => ({
    ...state,
    placeOrderStatus: { loading: true, success: false, error: '' }
  })),
  on(OrdersActions.placeOrderSuccess, (state, { cart }) => ({
    ...state,
    placeOrderStatus: { loading: false, success: true, error: '' },
    orders: [...state.orders, cart],
    cart: {
      items: [],
      placedOn: undefined,
      total: 0
    }
  })),
  on(OrdersActions.placeOrderFailure, (state, { error }) => ({
    ...state,
    placeOrderStatus: { loading: false, success: false, error: error.error }
  })),

  // Load Cart
  on(OrdersActions.loadCart, (state) => ({
    ...state,
    loadCartStatus: { loading: true, success: false, error: '' }
  })),
  on(OrdersActions.loadCartSuccess, (state, { cart }) => ({
    ...state,
    cart: cart,
    loadCartStatus: { loading: false, success: true, error: '' }
  })),
  on(OrdersActions.loadCartFailure, (state, { error }) => ({
    ...state,
    loadCartStatus: { loading: false, success: false, error: error }
  })),

  // Reset Order Placement Status
  on(OrdersActions.restPlaceOrderSuccess, (state) => ({
    ...state,
    placeOrderStatus: { loading: false, success: false, error: '' }
  }))
);
```

#### File 4: `orders.effects.ts`
- **Location:** `src/app/features/orders/state/orders.effects.ts`
- **Purpose:** Interacts with `OrdersService`, validates `userId`, and chains auto-reset for placement status.

```typescript
import { inject, Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { OrdersService } from "../services/orders-service";
import {
  addToCart, addToCartFailure, addToCartSucess,
  getOrders, getOrdersFailure, getOrdersSuccess,
  loadCart, loadCartFailure, loadCartSuccess,
  placeOrder, placeOrderFailure, placeOrderSuccess,
  restPlaceOrderSuccess
} from "./orders.actions";
import { catchError, map, of, switchMap, tap } from "rxjs";

@Injectable()
export class OrdersEffects {
  private actions$ = inject(Actions);
  private ordersService = inject(OrdersService);

  // Get orders history with guard against unauthenticated userId (0)
  private getOrders$ = createEffect(() =>
    this.actions$.pipe(
      ofType(getOrders),
      switchMap(({ userId }) => {
        if (userId === 0) {
          return of(getOrdersFailure({ error: 'User loading issue' }));
        }
        return this.ordersService.getOrders(userId).pipe(
          map((orders) => getOrdersSuccess(orders)),
          catchError((error) => of(getOrdersFailure({ error })))
        );
      })
    )
  );

  // Load active cart
  private getCart$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadCart),
      switchMap(({ userId }) =>
        this.ordersService.getCart(userId).pipe(
          map((cart) => loadCartSuccess(cart)),
          catchError((error) => of(loadCartFailure(error)))
        )
      )
    )
  );

  // Add item to cart passing productId forward to the success action
  private addToCart$ = createEffect(() =>
    this.actions$.pipe(
      ofType(addToCart),
      switchMap(({ userId, productId }) =>
        this.ordersService.addToCart(userId, productId).pipe(
          map((cart) => addToCartSucess(cart, productId)),
          catchError((error) => of(addToCartFailure({ error })))
        )
      )
    )
  );

  // Place order
  private placeOrder$ = createEffect(() =>
    this.actions$.pipe(
      ofType(placeOrder),
      switchMap(({ userId }) =>
        this.ordersService.placeOrder(userId).pipe(
          map((cart) => placeOrderSuccess(cart)),
          catchError(({ error }) => of(placeOrderFailure({ error })))
        )
      )
    )
  );

  // Reset order status after placing an order
  private restOrderPlaceSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(placeOrderSuccess),
      map(() => restPlaceOrderSuccess())
    )
  );
}
```

#### File 5: `orders.selector.ts`
- **Location:** `src/app/features/orders/state/orders.selector.ts`
- **Purpose:** Selectors for orders, cart, in-flight product IDs, and all action status trackers.

```typescript
import { createFeatureSelector, createSelector } from "@ngrx/store";
import OrdersState from "./orders.state";

export const ordersState = createFeatureSelector<OrdersState>('orders');
export const ordersSelector = createSelector(ordersState, (state) => state.orders);
export const getOrdersStatusSelector = createSelector(ordersState, (state) => state.getOrdersStatus);
export const cartSelectore = createSelector(ordersState, (state) => state.cart);
export const loadCartStatusSelector = createSelector(ordersState, (state) => state.loadCartStatus);
export const placeOrderStatusSelector = createSelector(ordersState, (state) => state.placeOrderStatus);
export const addToCartStatusSelector = createSelector(ordersState, (state) => state.addToCartStatus);
export const productsToAddSelector = createSelector(ordersState, (state) => state.productsToAdd);
```

---

### 4.3 Products Feature Slice (`features/products/state`)

The Products slice manages catalog viewing, admin addition, admin editing, and the navigation workflow for product editing.

#### File 1: `products.state.ts`
- **Location:** `src/app/features/products/state/products.state.ts`
- **Purpose:** State shape containing catalog list, editing buffer (`productToEdit`), and action status tracking.

```typescript
import { Status } from "../../auth/state/login.state";
import ProductDTO from "../models/productDTO";

export default interface ProductsState {
  products: ProductDTO[];
  getProductsStatus: Status;
  addProductStatus: Status;
  editProductStatus: Status;
  productToEdit: ProductDTO;
}

export const InitialProductsState: ProductsState = {
  products: [],
  productToEdit: {
    id: 0,
    name: '',
    category: '',
    price: 0,
    imgUrl: ''
  },
  getProductsStatus: { loading: false, success: false, error: '' },
  addProductStatus: { loading: false, success: false, error: '' },
  editProductStatus: { loading: false, success: false, error: '' }
};
```

#### File 2: `products.actions.ts`
- **Location:** `src/app/features/products/state/products.actions.ts`
- **Purpose:** Action creators for catalog retrieval, addition, editing, and initiating edit mode.

```typescript
import { createAction } from "@ngrx/store";

// Fetch catalog
export const getProducts = createAction('[PRODUCT] GET PRODUCTS');
export const getProductsSuccess = createAction('[PRODUCT] GET PRODUCTS SUCCESS', (products) => ({ products }));
export const getProductsFailure = createAction('[PRODUCT] GET PRODUCTS FAILURE', (error) => ({ error }));

// Add product
export const addProduct = createAction('[PRODUCT] ADD PRODUCT', (product) => ({ product }));
export const addProductSuccess = createAction('[PRODUCT] ADD PRODUCT SUCCESS', (product) => ({ product }));
export const addProductFailure = createAction('[PRODUCT] ADD PRODUCT FAILURE', (error) => ({ error }));

// Edit product
export const editProduct = createAction('[PRODUCT] EDIT PRODUCT', (product) => ({ product }));
export const editProductSuccess = createAction('[PRODUCT] EDIT PRODUCT SUCCESS', (product) => ({ product }));
export const editProductFailure = createAction('[PRODUCT] EDIT PRODUCT FAILURE', (error) => ({ error }));

// Route trigger to load product into edit buffer and navigate
export const initiateEditOnProduct = createAction('[PRODUCT] INITIATE EDIT', (product) => ({ product }));
```

#### File 3: `products.reducer.ts`
- **Location:** `src/app/features/products/state/products.reducer.ts`
- **Purpose:** Performs immutable catalog updates (array append, mapping item replacement) and manages the `productToEdit` buffer.

```typescript
import { createReducer, on } from "@ngrx/store";
import { InitialProductsState } from "./products.state";
import * as ProductActions from "./products.actions";

export const ProductsReducer = createReducer(
  InitialProductsState,

  // Get Products
  on(ProductActions.getProducts, (state) => ({
    ...state,
    getProductsStatus: { loading: true, success: false, error: '' }
  })),
  on(ProductActions.getProductsSuccess, (state, { products }) => ({
    ...state,
    products: products,
    getProductsStatus: { loading: false, success: true, error: '' }
  })),
  on(ProductActions.getProductsFailure, (state, { error }) => ({
    ...state,
    getProductsStatus: { loading: false, success: false, error: error }
  })),

  // Add Product
  on(ProductActions.addProduct, (state) => ({
    ...state,
    addProductStatus: { loading: true, success: false, error: '' }
  })),
  on(ProductActions.addProductSuccess, (state, { product }) => ({
    ...state,
    products: [...state.products, product],
    addProductStatus: { loading: false, success: true, error: '' }
  })),
  on(ProductActions.addProductFailure, (state, { error }) => ({
    ...state,
    addProductStatus: { loading: false, success: false, error: error }
  })),

  // Edit Product
  on(ProductActions.editProduct, (state) => ({
    ...state,
    editProductStatus: { loading: true, success: false, error: '' }
  })),
  on(ProductActions.editProductSuccess, (state, { product }) => ({
    ...state,
    products: state.products.map((p) => (p.id === product.id ? product : p)),
    editProductStatus: { loading: false, success: true, error: '' },
    productToEdit: { id: 0, name: '', category: '', price: 0, imgUrl: '' } // Clear edit buffer
  })),
  on(ProductActions.editProductFailure, (state, { error }) => ({
    ...state,
    editProductStatus: { loading: false, success: false, error: error.error }
  })),

  // Store target product in buffer
  on(ProductActions.initiateEditOnProduct, (state, { product }) => ({
    ...state,
    productToEdit: product
  }))
);
```

#### File 4: `products.effects.ts`
- **Location:** `src/app/features/products/state/products.effects.ts`
- **Purpose:** Connects to `ProductService` for API calls and handles post-operation navigation to `/products` and `/products/edit`.

```typescript
import { inject, Injectable } from "@angular/core";
import { ProductService } from "../services/product-service";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import {
  addProduct, addProductFailure, addProductSuccess,
  editProduct, editProductFailure, editProductSuccess,
  getProducts, getProductsFailure, getProductsSuccess,
  initiateEditOnProduct
} from "./products.actions";
import { catchError, map, of, switchMap, tap } from "rxjs";
import { Router } from "@angular/router";

@Injectable()
export class ProductsEffects {
  private productsService = inject(ProductService);
  private router: Router = inject(Router);
  private actions$ = inject(Actions);

  // Fetch catalog
  private getProducts$ = createEffect(() =>
    this.actions$.pipe(
      ofType(getProducts),
      switchMap(() =>
        this.productsService.getProducts().pipe(
          map((products) => getProductsSuccess(products)),
          catchError((error) => of(getProductsFailure({ error })))
        )
      )
    )
  );

  // Add product and redirect to /products
  private addProduct$ = createEffect(() =>
    this.actions$.pipe(
      ofType(addProduct),
      switchMap(({ product }) =>
        this.productsService.addProduct(product).pipe(
          map((savedProduct) => {
            this.router.navigate(['products']);
            return addProductSuccess(savedProduct);
          }),
          catchError((error) => of(addProductFailure({ error })))
        )
      )
    )
  );

  // Edit product and redirect to /products
  private editProduct$ = createEffect(() =>
    this.actions$.pipe(
      ofType(editProduct),
      switchMap(({ product }) =>
        this.productsService.editProduct(product).pipe(
          map((updatedProduct) => {
            this.router.navigate(['products']);
            return editProductSuccess(updatedProduct);
          }),
          catchError((error) => of(editProductFailure({ error })))
        )
      )
    )
  );

  // Navigate to edit form when editing is initiated
  private initiateEdit$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(initiateEditOnProduct),
        tap(() => {
          this.router.navigate(['products/edit']);
        })
      ),
    { dispatch: false }
  );
}
```

#### File 5: `products.selectors.ts`
- **Location:** `src/app/features/products/state/products.selectors.ts`
- **Purpose:** Memoized selectors for products catalog, active editing buffer, and statuses.

```typescript
import { createFeatureSelector, createSelector } from "@ngrx/store";
import ProductsState from "./products.state";

export const productsState = createFeatureSelector<ProductsState>('products');
export const productsSelector = createSelector(productsState, (state) => state.products);
export const getProductsStatusSelector = createSelector(productsState, (state) => state.getProductsStatus);
export const addProductStatusSelector = createSelector(productsState, (state) => state.addProductStatus);
export const editProductStatusSelector = createSelector(productsState, (state) => state.editProductStatus);
export const productToEditSelector = createSelector(productsState, (state) => state.productToEdit);
```

---

### 4.4 User Feature Slice (`features/user/state`)

The User slice stores the identity of the currently authenticated user (`id`, `userName`, `role`, `cart`), handles automatic JWT expiration checks, and exposes user data to other slices.

#### File 1: `user.state.ts`
- **Location:** `src/app/features/user/state/user.state.ts`
- **Purpose:** State contract for currently logged-in user profile and loading status.

```typescript
import { Status } from "../../auth/state/login.state";
import UserResDTO from "../models/UserResDTO";

export default interface UserState {
  currentUser: UserResDTO;
  userLoadingStatus: Status;
}

export const InitialUserState: UserState = {
  currentUser: {
    id: 0,
    userName: '',
    role: '',
    cart: 0
  },
  userLoadingStatus: { loading: false, success: false, error: '' }
};
```

#### File 2: `user.actions.ts`
- **Location:** `src/app/features/user/state/user.actions.ts`
- **Purpose:** Action creators for retrieving current user and clearing user state on logout.

```typescript
import { createAction, props } from "@ngrx/store";
import UserResDTO from "../models/UserResDTO";

export const loadUser = createAction('[USER] LOAD USER');
export const loadUserSuccess = createAction('[USER] LOAD USER SUCCESS', props<{ user: UserResDTO }>());
export const loadUserFailure = createAction('[USER] LOAD USER FAILURE', (error) => ({ error }));
export const eraseUser = createAction('[USER] ERASE USER');
```

#### File 3: `user.reducer.ts`
- **Location:** `src/app/features/user/state/user.reducer.ts`
- **Purpose:** Pure reducer storing `currentUser` or resetting it back to default zero values.

```typescript
import { createReducer, on } from "@ngrx/store";
import { InitialUserState } from "./user.state";
import * as UserActions from "./user.actions";

export const UserReducer = createReducer(
  InitialUserState,
  on(UserActions.loadUser, (state) => ({
    ...state,
    userLoadingStatus: { loading: true, success: false, error: '' }
  })),
  on(UserActions.loadUserSuccess, (state, { user }) => ({
    ...state,
    currentUser: user,
    userLoadingStatus: { loading: false, success: true, error: '' }
  })),
  on(UserActions.loadUserFailure, (state, { error }) => ({
    ...state,
    userLoadingStatus: { loading: false, success: false, error: error }
  })),
  on(UserActions.eraseUser, (state) => ({
    ...state,
    userLoadingStatus: { loading: false, success: false, error: '' },
    currentUser: {
      id: 0,
      userName: '',
      role: '',
      cart: 0
    }
  }))
);
```

#### File 4: `user.effect.ts`
- **Location:** `src/app/features/user/state/user.effect.ts`
- **Purpose:** Calls `UserService.loadUser()`, and detects expired JWT tokens, automatically dispatching `logout()` to initiate global logout.

```typescript
import { inject, Injectable } from "@angular/core";
import { UserService } from "../services/user-service";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { loadUser, loadUserFailure, loadUserSuccess } from "./user.actions";
import { catchError, map, of, switchMap, tap } from "rxjs";
import { Store } from "@ngrx/store";
import { logout } from "../../auth/state/login.action";

@Injectable()
export class userEffects {
  userService: UserService = inject(UserService);
  store: Store = inject(Store);
  actions$ = inject(Actions);

  private loadUser$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(loadUser),
      switchMap(() => {
        return this.userService.loadUser().pipe(
          map((user) => loadUserSuccess({ user })),
          catchError(({ error }) => {
            // Automatic session eviction on token expiration
            if (error.error === "JWT token expired") {
              this.store.dispatch(logout());
            }
            return of(loadUserFailure({ error }));
          })
        );
      })
    );
  });
}
```

#### File 5: `user.selector.ts`
- **Location:** `src/app/features/user/state/user.selector.ts`
- **Purpose:** Exposes selectors for `currentUser` and `userLoadingStatus`.

```typescript
import { createFeatureSelector, createSelector } from "@ngrx/store";
import UserState from "./user.state";

export const userState = createFeatureSelector<UserState>('user');
export const currentUserSelector = createSelector(userState, (state) => state.currentUser);
export const loadingUserStatusSelector = createSelector(userState, (state) => state.userLoadingStatus);
```

---

## 5. Cross-Domain Coordination & Workflow Bridges

A prominent pattern in this codebase is **inter-feature action bridging**:

```
 ┌──────────────────────┐                     ┌─────────────────────┐
 │    LoginEffects      │─── [USER] ERASE USER ──▶️│     UserReducer     │
 │    (on logout)       │                         │ (clears currentUser)│
 └──────────────────────┘                         └─────────────────────┘
            ▲                                                │
            │               [LOGIN] LOGOUT                   │
            └────────────── (token expired) ─────────────────┘
                                   │
                           ┌───────────────┐
                           │  userEffects  │
                           └───────────────┘
```

1. **Logout Waterfall:** When `logout` is dispatched:
   - `loginReducer` purges `localStorage` (`token`, `role`) and resets `isLoggedIn: false`.
   - `LoginEffects.logout$` redirects to `/login` and dispatches `eraseUser()`.
   - `UserReducer` resets `currentUser` to empty defaults (`id: 0`).
2. **Session Expiration Trigger:** When `loadUser` receives HTTP 401 with `"JWT token expired"`:
   - `userEffects` immediately dispatches `logout()` to terminate the session across the entire app.
3. **Session Hydration Cascade (`HeaderComponent`):**
   - On page refresh, if `token` exists in `localStorage`, `HeaderComponent` dispatches `loginSuccess(null)`.
   - `HeaderComponent` detects `user.id === 0` and dispatches `loadUser()`.
   - Once `user.id !== 0` arrives, `HeaderComponent` dispatches `loadCart(user.cart)` to hydrate the cart.

---

## 6. Store Consumption Patterns

### 6.1 Route Guards with NgRx Selectors

The codebase illustrates reading NgRx state directly inside functional route guards. For example, [`editGuard.ts`](file:///e:/079_data/develop/hartford-myspace/hartford-training-fullstack/miniprojects/01-ecom-monolith/client/src/app/guards/editGuard.ts) verifies that a product was selected for edit before allowing access to the `/products/edit` route:

```typescript
// src/app/guards/editGuard.ts
import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { Store } from "@ngrx/store";
import { Observable } from "rxjs";
import ProductDTO from "../features/products/models/productDTO";
import { productToEditSelector } from "../features/products/state/products.selectors";

export const editGuard: CanActivateFn = () => {
  const store: Store = inject(Store);
  const router: Router = inject(Router);
  const product$: Observable<ProductDTO> = store.select(productToEditSelector);

  let id;
  product$.subscribe((product) => {
    if (product) {
      id = product.id;
    }
  });

  if (id === 0) {
    router.navigate(['products']);
    return false;
  }
  return true;
};
```

---

### 6.2 Standalone Components with Async Pipe & Dispatch

#### Example 1: `ShowProductsComponent` (Catalog Fetching & User Loading)
```typescript
// src/app/features/products/components/show-products-component/show-products-component.ts
@Component({
  selector: 'app-show-products-component',
  imports: [AsyncPipe, ProductComponent],
  templateUrl: './show-products-component.html',
  styleUrl: './show-products-component.css',
})
export class ShowProductsComponent implements OnInit {
  private store: Store = inject(Store);
  products$!: Observable<ProductDTO[]>;
  getProductsStatus$!: Observable<Status>;

  ngOnInit() {
    this.products$ = this.store.select(productsSelector);
    this.getProductsStatus$ = this.store.select(getProductsStatusSelector);
    this.store.dispatch(getProducts());
    this.store.dispatch(loadUser());
  }
}
```

#### Example 2: `ProductComponent` (Granular Loading State for Add to Cart)
The component binds to `productsToAddSelector` to show a per-product loading indicator:
```typescript
// src/app/features/products/components/product-component/product-component.ts
export class ProductComponent implements OnInit {
  @Input() product!: ProductDTO;
  private store: Store = inject(Store);
  productsToAdd$!: Observable<number[]>;
  currentUser$!: Observable<UserResDTO>;
  userId!: number;

  ngOnInit() {
    this.productsToAdd$ = this.store.select(productsToAddSelector);
    this.currentUser$ = this.store.select(currentUserSelector);
  }

  addToCart(productId: number | undefined) {
    this.currentUser$.subscribe((user) => {
      if (user) {
        this.userId = user.id;
      }
    });
    this.store.dispatch(addToCart(this.userId, productId));
  }

  edit() {
    this.store.dispatch(initiateEditOnProduct(this.product));
  }
}
```

---

## 7. Conventions, Codebase Nuances & Best Practice Review

When reviewing or replicating this implementation across new projects, take note of the following observations and improvements:

| Topic | Current Codebase Implementation | Recommended Standard / Best Practice |
| :--- | :--- | :--- |
| **Action Payload Syntax** | Mixed usage: arrow functions `(val) => ({ val })` in Auth/Orders/Products, vs `props<{ user: UserResDTO }>()` in User. | Adopt `props<{...}>()` consistently across all action definitions for uniform typing. |
| **Action & Selector Naming** | Slight naming variations: `cartSelectore` (typo), `addToCartSucess` (typo), `restPlaceOrderSuccess` (`rest` vs `reset`). | Correct identifiers to `cartSelector`, `addToCartSuccess`, and `resetPlaceOrderStatus`. |
| **File Pluralization** | Slight inconsistencies: `user.effect.ts` (singular) vs `orders.effects.ts` (plural); `products.selectors.ts` (plural) vs `orders.selector.ts` (singular). | Standardize on plural names: `*.actions.ts`, `*.effects.ts`, `*.selectors.ts`. |
| **Component Subscriptions** | Manual `.subscribe()` inside components (e.g. `currentUser$.subscribe(...)` inside `addToCart()`). | Use RxJS `take(1)` or pipeline operators (`withLatestFrom` / signals) to avoid unbounded manual subscriptions. |
| **Route Guard Reactive Stream** | Guard subscribes synchronously to an observable rather than returning an `Observable<boolean>`. | Return `store.select(productToEditSelector).pipe(map(p => p.id !== 0))` directly from the `CanActivateFn`. |

---

## 🎯 Quick-Start Template for a New Feature

To add a new feature (e.g., `Reviews`), duplicate this 5-file pattern in `src/app/features/reviews/state/`:

1. **`reviews.state.ts`**: Declare `ReviewsState` with domain data + `Status` objects.
2. **`reviews.actions.ts`**: Define `loadReviews`, `loadReviewsSuccess`, `loadReviewsFailure`.
3. **`reviews.reducer.ts`**: Handle immutability with spread operators and update status flags.
4. **`reviews.effects.ts`**: Implement `loadReviews$` with `switchMap` and inner `catchError`.
5. **`reviews.selector.ts`**: Export feature and property memoized selectors.
6. **`app.config.ts`**: Register `reviews: ReviewsReducer` in `provideStore` and `ReviewsEffects` in `provideEffects`.

---

## api docs and token format
- read the complete API_DOCUMENTATION
- refer the PROEJCT_PLAN to get clarity on the project
- read the tests from the VERIFIED_TESTS, these are the exact API calls.
- refer the TOKEN to know what is in the JWT token we get from BE from TOKEN_DETAILS

## evaluation criterion
refer the RUBRIC for the evaluation creterion, and make sure our project. as far as Frontend is concerned, not touch backend

## prefered dependencies versions - package.json
these are the preference dependecny version , from a refernce project, use them:
```json
    {
    "name": "ninth",
    "version": "0.0.0",
    "scripts": {
        "ng": "ng",
        "start": "ng serve",
        "build": "ng build",
        "watch": "ng build --watch --configuration development",
        "test": "ng test",
        "serve:ssr:test-app": "node dist/test-app/server/server.mjs"
    },
    "private": true,
    "packageManager": "npm@11.6.2",
    "overrides": {
        "browserslist": "4.28.4",
        "baseline-browser-mapping": "2.10.38",
        "node-releases": "2.0.47"
    },
    "dependencies": {
        "@angular/common": "21.2.17",
        "@angular/compiler": "21.2.17",
        "@angular/core": "21.2.17",
        "@angular/forms": "21.2.17",
        "@angular/platform-browser": "21.2.17",
        "@angular/platform-server": "21.2.17",
        "@angular/router": "21.2.17",
        "@angular/ssr": "21.2.17",
        "@ngrx/effects": "^21.1.1",
        "@ngrx/store": "^21.1.1",
        "express": "^5.1.0",
        "rxjs": "~7.8.0",
        "tslib": "^2.3.0",
        "zone.js": "^0.16.2"
    },
    "devDependencies": {
        "@angular/build": "21.2.17",
        "@angular/cli": "21.2.17",
        "@angular/compiler-cli": "21.2.17",
        "@tailwindcss/postcss": "^4.1.12",
        "@types/express": "^5.0.1",
        "@types/node": "^20.17.19",
        "jsdom": "^28.0.0",
        "postcss": "^8.5.6",
        "prettier": "^3.8.1",
        "tailwindcss": "^4.1.12",
        "typescript": "~5.9.2",
        "vitest": "^4.0.8"
    }
    }

```

## General rules:
1. dont write comments everywhere, rarely right comments when u feel the function name itself wont explain eveything it does, and it is important to understand the distinction
2. dont ever user emojis, any where in the codebase
3. keep the implementation, simple and crips, dont write fallbacks -> for fallback -> for fallback, making code unreadable, over engineered, and easily detectable by humans as AI generated codebase.
4. follow the implementation practices i gave above.


## Automated Frontend Testing — Playwright

After completing the UI implementation, perform **comprehensive end-to-end testing using Playwright with TypeScript/Node.js**. **Do not use Python or Python Playwright.**

Create a dedicated `playwright-testing` folder, initialize it as an npm project, install `@playwright/test`, and configure Playwright for the locally running application.

Use the file **`PROEJCT_PLAN` as the authoritative testing specification**. First, thoroughly inspect `PROEJCT_PLAN` and derive a complete **test coverage matrix** from it. Identify **every user/role, permission, feature, page, workflow, dependency, state transition, validation rule, CRUD operation, navigation path, success condition, failure condition, and business rule** described in the file. Nothing in `PROEJCT_PLAN` should be skipped merely because it is not a "major" flow.

For **each user/role**, test the application from that user's perspective and execute their journeys **end-to-end**, starting from authentication where applicable and continuing through all actions required to complete the workflow. Verify both the UI behavior and the resulting application state. Cover role-based access control, authorized and unauthorized actions, redirects, session handling, logout, protected routes, and interactions between different users when a workflow involves multiple roles.

For every feature identified in `PROEJCT_PLAN`, test the complete lifecycle rather than isolated components. Cover realistic browser interactions including page navigation, clicks, typing, dropdowns, tables, search/filter/sort, pagination, file uploads where applicable, modals, notifications, loading states, empty states, confirmations, redirects, and dynamically updated UI. Test **create, read, update, delete, approve/reject, submit/cancel, assign/unassign, and other state-changing operations** wherever applicable.

Include comprehensive **positive, negative, boundary, validation, and error-path testing**. Verify required fields, invalid formats, incorrect credentials, duplicate records, nonexistent records, unauthorized access, invalid transitions, server/API failures, empty responses, loading behavior, expired sessions, and other edge cases that can be inferred from `PROEJCT_PLAN` or the implemented application.

Use the real locally running frontend and backend rather than mocking the complete application flow. Interact with the application through the browser as an actual user would. Test against the real local database and freely create, modify, and delete test data as necessary. Where workflows depend on multiple users, create the required records and execute the workflow across those users in the correct sequence.

Organize the test suite by **user role → feature → end-to-end journey**, while keeping reusable fixtures, authentication helpers, test data utilities, and page/component abstractions separate. Avoid writing shallow tests that only verify that a page loads; assert meaningful outcomes and state changes after each important action.

Run the complete Playwright suite against the locally running application. Investigate every failure and determine whether it is caused by the test, frontend, backend integration, routing, authentication, validation, or another implementation issue. **Fix the underlying implementation issues when they are genuine application defects**, then rerun the affected tests and the broader regression suite.

Continue the test-and-fix cycle until all functionality covered by `PROEJCT_PLAN` has been exercised and the implemented flows pass. Do not simply skip, disable, weaken, or comment out failing tests to obtain a passing result.

At the end, produce a concise test summary containing:
- Total test cases executed
- Passed / failed / skipped tests
- Coverage mapped to every section and user journey in `PROEJCT_PLAN`
- Defects discovered and fixes applied
- Any remaining limitations or genuinely untestable requirements
- Commands required to run the Playwright suite locally

The final Playwright suite should represent **full application-level E2E coverage derived from `PROEJCT_PLAN`**, not just a collection of basic smoke tests.

## using running context
use the RUNNING_CONTEXT TO understand the current state of the project, after each meaningfull big enough step, update the details into it, so that, in case you run out of context, or another agent has to continue in the future.

---
context:
CLIENT_CODE_LOCATION -> 
PROEJCT_PLAN- containing user journey mapping->
RUNNING_CONTEXT ->
API_DOCUMENTATION -> 
TOKEN_DETAILS -> 
RUBRIC -> 
FLOWCHARTS -> 
VERIFIED_TESTS ->  
GEMINI CONTEXT -> 
