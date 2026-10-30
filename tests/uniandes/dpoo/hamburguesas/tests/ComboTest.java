package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest {
	private ArrayList<ProductoMenu> items;

    private Combo combo;

    @BeforeEach
    void setUp( ) throws Exception {
        items = new ArrayList<ProductoMenu>( );
        items.add( new ProductoMenu( "corral", 14000 ) );
        items.add( new ProductoMenu( "papas medianas", 5500 ) );
        items.add( new ProductoMenu( "gaseosa", 5000 ) );
        combo = new Combo( "combo corral", 0.1, items );
    }

    @AfterEach
    void tearDown( ) throws Exception
    {
    }

    @Test
    void testGetNombre( )
    {
        assertEquals( "combo corral", combo.getNombre( ), "El nombre del combo no es el esperado." );
    }

    @Test
    void testGetPrecioConDescuento( )
    {
        // 24500 con 10% de descuento, el cliente paga el 90%
        assertEquals( 22050, combo.getPrecio( ), "El precio del combo debe ser el de los productos menos el descuento." );
    }

    @Test
    void testGetPrecioSinDescuento( )
    {
        Combo sinDescuento = new Combo( "combo sin descuento", 0.0, items );
        assertEquals( 24500, sinDescuento.getPrecio( ), "Sin descuento el precio debe ser la suma de los productos." );
    }

    @Test
    void testGetPrecioMitad( )
    {
        Combo mitad = new Combo( "combo mitad", 0.5, items );
        assertEquals( 12250, mitad.getPrecio( ), "Con 50% de descuento el precio debe ser la mitad." );
    }

    @Test
    void testGetPrecioDescuentoTotal( )
    {
        Combo gratis = new Combo( "combo gratis", 1.0, items );
        assertEquals( 0, gratis.getPrecio( ), "Con 100% de descuento el precio debe ser 0." );
    }

    @Test
    void testGetPrecioDescuentoDelEnunciado( )
    {
        //hamburguesa sencilla y papas medianas con 7% de descuento
        ArrayList<ProductoMenu> productos = new ArrayList<ProductoMenu>( );
        productos.add( new ProductoMenu( "corral", 14000 ) );
        productos.add( new ProductoMenu( "papas medianas", 5500 ) );
        Combo especial = new Combo( "combo especial", 0.07, productos );

        int esperado = (int) ( 19500 * 0.93 ); // 18135
        assertEquals( esperado, especial.getPrecio( ), "El precio con 7% de descuento no es el esperado." );
        assertEquals( 18135, especial.getPrecio( ), "El precio con 7% de descuento no es el esperado." );
    }

    @Test
    void testGetPrecioTruncaDecimales( )
    {
        // 34500 con 9.5% de descuento = 31222.5, que debe truncarse a 31222
        ArrayList<ProductoMenu> productos = new ArrayList<ProductoMenu>( );
        productos.add( new ProductoMenu( "especial", 24000 ) );
        productos.add( new ProductoMenu( "papas medianas", 5500 ) );
        productos.add( new ProductoMenu( "gaseosa", 5000 ) );
        Combo especial = new Combo( "combo especial", 0.095, productos );

        assertEquals( 31222, especial.getPrecio( ), "El precio debe truncarse a un valor entero." );
    }

    @Test
    void testGetPrecioComboSinProductos( )
    {
        Combo vacio = new Combo( "combo vacio", 0.1, new ArrayList<ProductoMenu>( ) );
        assertEquals( 0, vacio.getPrecio( ), "Un combo sin productos debe costar 0." );
    }

    @Test
    void testComboNoDependeDeLaListaOriginal( )
    {
        items.add( new ProductoMenu( "postre", 10000 ) );
        items.remove( 0 );

        assertEquals( 22050, combo.getPrecio( ), "Modificar la lista original no debe afectar el combo ya creado." );
    }

    @Test
    void testGenerarTextoFactura( )
    {
        String esperado = "Combo combo corral\n" + " Descuento: 0.1\n" + "            22050\n";
        assertEquals( esperado, combo.generarTextoFactura( ), "El texto de la factura del combo no es el esperado." );
    }

    @Test
    void testGenerarTextoFacturaOtroCombo( )
    {
        Combo mitad = new Combo( "combo mitad", 0.5, items );
        String esperado = "Combo combo mitad\n" + " Descuento: 0.5\n" + "            12250\n";
        assertEquals( esperado, mitad.generarTextoFactura( ), "El texto de la factura del combo no es el esperado." );
    }

}
