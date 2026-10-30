package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoMenuTest {
	private ProductoMenu producto;

    @BeforeEach
    void setUp( ) throws Exception {
        producto = new ProductoMenu( "corral", 14000 );
    }

    @AfterEach
    void tearDown( ) throws Exception {
    }

    @Test
    void testGetNombre( ) {
        assertEquals( "corral", producto.getNombre( ), "El nombre del producto no es el esperado." );
    }

    @Test
    void testGetPrecio( ) {
        assertEquals( 14000, producto.getPrecio( ), "El precio del producto no es el esperado." );
    }

    @Test
    void testGenerarTextoFactura( ) {
        String esperado = "corral\n" + "            14000\n";
        assertEquals( esperado, producto.generarTextoFactura( ), "El texto de la factura del producto no es el esperado." );
    }

    @Test
    void testNombreCompuestoYPrecioCero( ) {
        ProductoMenu otro = new ProductoMenu( "papas en casco medianas", 0 );

        assertEquals( "papas en casco medianas", otro.getNombre( ), "El nombre del producto no es el esperado." );
        assertEquals( 0, otro.getPrecio( ), "El precio del producto no es el esperado." );
        assertEquals( "papas en casco medianas\n" + "            0\n", otro.generarTextoFactura( ), "El texto de la factura del producto no es el esperado." );
    }

    @Test
    void testProductosIndependientes( ) {
        ProductoMenu otro = new ProductoMenu( "gaseosa", 5000 );

        assertEquals( "corral", producto.getNombre( ), "Crear otro producto no debe alterar el primero." );
        assertEquals( 14000, producto.getPrecio( ), "Crear otro producto no debe alterar el primero." );
        assertEquals( "gaseosa", otro.getNombre( ), "El nombre del segundo producto no es el esperado." );
        assertEquals( 5000, otro.getPrecio( ), "El precio del segundo producto no es el esperado." );
    }

}
