```mermaid
    erDiagram
    %% Bloque de catalogo
    CATEGORIA ||--o{ PRODUCTO : "pertenece"
    STOCK ||--|| PRODUCTO : "posee"

    %% Bloque de ingresos
    PROVEEDOR ||--o{ INGRESO: "provee"
    USUARIO ||--o{ INGRESO: "registra"   
    INGRESO ||--|{ DETALLE_INGRESO : "ingresa"
    DETALLE_INGRESO }o--|| PRODUCTO : "agrega"
    DETALLE_INGRESO }o--|| STOCK : "suma"
    
    %% Bloque de venta
    CLIENTE ||--o{ VENTA : "recibe"
    USUARIO ||--o{ VENTA: "registra" 
    VENTA ||--|{ DETALLE_VENTA : "vende"
    DETALLE_VENTA }o--|| PRODUCTO : "contiene"
    DETALLE_VENTA }o--|| STOCK : "reduce"   
    
    %% Bloque de ajustes
    USUARIO ||--o{ AJUSTE_STOCK : "registra"
    AJUSTE_STOCK }o--|| PRODUCTO : "ajusta" 
    AJUSTE_STOCK }o--|| STOCK : "modifica"
    AJUSTE_STOCK }o--|| MOTIVO : "motivo"
    
    CATEGORIA {
        serial id PK
        varchar(50) categoria
    }

    PRODUCTO{
        serial id PK
        varchar(50) producto
        boolean activo
        money precio_mayorista
        money precio_venta
        integer id_categoria FK
    }

    PROVEEDOR {
        serial id PK
        varchar(50) proveedor
        varchar(50) corredor
        vechar(20) telefono
        boolean activo
    }

    CLIENTE {
        serial id PK
        varchar(50) nyap
        vechar(20) telefono
        boolean activo
    }

    STOCK {
        integer id PK
        integer id_producto PK, FK
        integer stock
    }

    INGRESO {
        serial id PK
        date fecha
        integer id_usuario FK
        integer id_proveedor FK
    }

    DETALLE_INGRESO {
        serial id PK
        integer id_ingreso FK
        integer id_producto FK
        integer cantidad
        money precio_mayorista    
    }

    VENTA {
        serial id PK
        date fecha
        boolean pagado
        integer id_usuario FK

    }

    DETALLE_VENTA {
        serial id PK
        integer id_venta FK
        integer id_producto FK
        integer cantidad 
        money precio_venta
    }

    AJUSTE_STOCK {
        serial id PK
        date fecha
        integer id_producto FK
        integer cantidad
        integer id_motivo FK
        integer id_usuario FK
        varchar(50) detalles
    }

    MOTIVO {
        serial id PK
        varchar(50) motivo
        boolean afecta_positivo
    }

    USUARIO {
        serial id PK
        varchar(50) usuario
        varchar contraseña
        varchar(50) rol
        boolean activo
    }
  
```

<!--
    USUARIO ||--o{ PEDIDO : "realiza"
    PEDIDO ||--|{ LINEA_PEDIDO : "contiene"
    PRODUCTO ||--o{ LINEA_PEDIDO : "aparece en"

     \|\|--\|\|	Uno a uno
    \|\|--o{	Uno a muchos
    }o--o{	Muchos a muchos
    \|\|--o\|	Uno a cero o uno

    o: opcional
    | obligatoria -->
