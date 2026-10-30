package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class PedidoTest {
	private Pedido pedido;
    private ProductoMenu corral;
    private ProductoMenu papas;
    private ProductoMenu gaseosa;
    private File archivoTemporal;

    @BeforeEach
    void setUp( ) throws Exception {
        pedido = new Pedido( "Andrea Pérez", "Calle 1 # 46-3" );
        corral = new ProductoMenu( "corral", 14000 );
        papas = new ProductoMenu( "papas medianas", 5500 );
        gaseosa = new ProductoMenu( "gaseosa", 5000 );
        archivoTemporal = null;
    }

    @AfterEach
    void tearDown( ) throws Exception {
        if( archivoTemporal != null && archivoTemporal.exists( ) ) {
            archivoTemporal.delete( );
        }
    }

    private String leerArchivo( File archivo ) throws IOException {
        return new String( Files.readAllBytes( archivo.toPath( ) ), Charset.defaultCharset( ) );
    }


    @Test
    void testGetNombreCliente( ) {
        assertEquals( "Andrea Pérez", pedido.getNombreCliente( ), "El nombre del cliente no es el esperado." );
    }

    @Test
    void testIdentificadoresConsecutivos( ) {
        Pedido segundo = new Pedido( "Luis", "Carrera 5" );
        Pedido tercero = new Pedido( "Marta", "Avenida 9" );

        assertEquals( pedido.getIdPedido( ) + 1, segundo.getIdPedido( ), "El segundo pedido debe tener el siguiente identificador." );
        assertEquals( segundo.getIdPedido( ) + 1, tercero.getIdPedido( ), "El tercer pedido debe tener el siguiente identificador." );
    }

    @Test
    void testIdentificadorNoCambia( ) {
        int id = pedido.getIdPedido( );
        new Pedido( "Luis", "Carrera 5" );
        assertEquals( id, pedido.getIdPedido( ), "Crear otros pedidos no debe cambiar el identificador de uno existente." );
    }


    @Test
    void testPrecioTotalPedidoVacio( ) {
        assertEquals( 0, pedido.getPrecioTotalPedido( ), "Un pedido sin productos debe costar 0." );
    }

    @Test
    void testPrecioTotalUnProducto( ) {
        pedido.agregarProducto( corral );

        assertEquals( 16660, pedido.getPrecioTotalPedido( ), "El total debe ser el neto más el 19% de IVA." );
    }

    @Test
    void testPrecioTotalVariosProductos( ) {
        pedido.agregarProducto( corral );
        pedido.agregarProducto( papas );
        pedido.agregarProducto( gaseosa );

        assertEquals( 29155, pedido.getPrecioTotalPedido( ), "El total debe sumar todos los productos más el IVA." );
    }

    @Test
    void testPrecioTotalMismoProductoVariasVeces( ) {
        pedido.agregarProducto( gaseosa );
        pedido.agregarProducto( gaseosa );

        assertEquals( 11900, pedido.getPrecioTotalPedido( ), "Un mismo producto agregado dos veces se cobra dos veces." );
    }

    @Test
    void testIvaSeTruncaAEntero( ) {
        pedido.agregarProducto( new ProductoMenu( "producto raro", 5001 ) );

        assertEquals( 5951, pedido.getPrecioTotalPedido( ), "El IVA debe redondearse a un valor entero." );
    }

    @Test
    void testPrecioTotalPedidoMixto( ) {
        pedido.agregarProducto( corral );
        pedido.agregarProducto( crearCombo( ) );
        pedido.agregarProducto( crearProductoAjustado( ) );

        assertEquals( 62534, pedido.getPrecioTotalPedido( ), "El total del pedido no es el esperado." );
    }

    
    @Test
    void testGenerarTextoFacturaPedidoVacio( )
    {
        String esperado = "Cliente: Andrea Pérez\n" + "Dirección: Calle 1 # 46-3\n" + "----------------\n" + "----------------\n" + "Precio Neto:  0\n" + "IVA:          0\n"
                + "Precio Total: 0\n";
        assertEquals( esperado, pedido.generarTextoFactura( ), "La factura de un pedido vacío no es la esperada." );
    }

    @Test
    void testGenerarTextoFacturaProductosBasicos( )
    {
        pedido.agregarProducto( corral );
        pedido.agregarProducto( papas );

        String esperado = "Cliente: Andrea Pérez\n" + "Dirección: Calle 1 # 46-3\n" + "----------------\n" + "corral\n" + "            14000\n" + "papas medianas\n" + "            5500\n"
                + "----------------\n" + "Precio Neto:  19500\n" + "IVA:          3705\n" + "Precio Total: 23205\n";
        assertEquals( esperado, pedido.generarTextoFactura( ), "La factura con productos básicos no es la esperada." );
    }

    @Test
    void testGenerarTextoFacturaPedidoMixto( )
    {
        pedido.agregarProducto( corral );
        pedido.agregarProducto( crearCombo( ) );
        pedido.agregarProducto( crearProductoAjustado( ) );

        String esperado = "Cliente: Andrea Pérez\n" + "Dirección: Calle 1 # 46-3\n" + "----------------\n" + "corral\n" + "            14000\n" + "Combo combo corral\n" + " Descuento: 0.1\n"
                + "            22050\n" + "corral\n" + "    +queso mozzarella                2500\n" + "            16500\n" + "----------------\n" + "Precio Neto:  52550\n"
                + "IVA:          9984\n" + "Precio Total: 62534\n";
        assertEquals( esperado, pedido.generarTextoFactura( ), "La factura del pedido mixto no es la esperada." );
    }


    
    @Test
    void testGuardarFactura( ) throws IOException
    {
        pedido.agregarProducto( corral );
        pedido.agregarProducto( crearCombo( ) );
        archivoTemporal = File.createTempFile( "factura_prueba", ".txt" );
        archivoTemporal.delete( );

        pedido.guardarFactura( archivoTemporal );

        assertTrue( archivoTemporal.exists( ), "El archivo de la factura debe haberse creado." );
        assertEquals( pedido.generarTextoFactura( ), leerArchivo( archivoTemporal ), "El contenido del archivo debe ser el texto de la factura." );
    }

    @Test
    void testGuardarFacturaPedidoVacio( ) throws IOException
    {
        archivoTemporal = File.createTempFile( "factura_prueba", ".txt" );

        pedido.guardarFactura( archivoTemporal );

        assertEquals( pedido.generarTextoFactura( ), leerArchivo( archivoTemporal ), "El contenido del archivo debe ser el texto de la factura." );
    }

    @Test
    void testGuardarFacturaSobrescribeArchivoExistente( ) throws IOException
    {
        archivoTemporal = File.createTempFile( "factura_prueba", ".txt" );
        Files.write( archivoTemporal.toPath( ), "contenido anterior que debe desaparecer, bastante más largo que la factura de un pedido vacío o casi".getBytes( ) );

        pedido.guardarFactura( archivoTemporal );

        assertEquals( pedido.generarTextoFactura( ), leerArchivo( archivoTemporal ), "El archivo anterior debe reemplazarse completamente." );
    }

    @Test
    void testGuardarFacturaEnCarpetaInexistente( )
    {
        File archivo = new File( "data/carpeta_que_no_existe/factura.txt" );

        assertThrows( FileNotFoundException.class, ( ) -> pedido.guardarFactura( archivo ), "Debe lanzar FileNotFoundException si no se puede crear el archivo." );
        assertTrue( !archivo.exists( ), "No debe haberse creado ningún archivo." );
    }


    
    private Combo crearCombo( )
    {
        ArrayList<ProductoMenu> items = new ArrayList<ProductoMenu>( );
        items.add( corral );
        items.add( papas );
        items.add( gaseosa );
        return new Combo( "combo corral", 0.1, items );
    }


    private ProductoAjustado crearProductoAjustado( )
    {
        ProductoAjustado ajustado = new ProductoAjustado( corral );
        try
        {
            java.lang.reflect.Field campo = ProductoAjustado.class.getDeclaredField( "agregados" );
            campo.setAccessible( true );
            @SuppressWarnings( "unchecked" )
            ArrayList<Ingrediente> agregados = (ArrayList<Ingrediente>) campo.get( ajustado );
            agregados.add( new Ingrediente( "queso mozzarella", 2500 ) );
        }
        catch( Exception e )
        {
            throw new RuntimeException( "No se pudo preparar el producto ajustado: " + e.getMessage( ), e );
        }
        return ajustado;
    }

}
