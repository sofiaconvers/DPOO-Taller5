package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.excepciones.HamburguesaException;
import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;

public class RestauranteTest {
	private static final String CARPETA_PRUEBAS = "data/pruebas/";
    private static final String CARPETA_FACTURAS = "./facturas/";
    private static final String PREFIJO_FACTURAS = "factura_";
    private Restaurante restaurante;

    private ArrayList<File> facturasCreadas;

    @BeforeEach
    void setUp( ) throws Exception {
        restaurante = new Restaurante( );
        facturasCreadas = new ArrayList<File>( );
    }

    @AfterEach
    void tearDown( ) throws Exception {
        for( File f : facturasCreadas ) {
            if( f.exists( ) ) {
                f.delete( );
            }
        }
    }

    private File archivo( String nombre ) {
        return new File( CARPETA_PRUEBAS + nombre );
    }

    private void cargarArchivosOk( ) throws Exception {
        restaurante.cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_ok.txt" ) );
    }

    private File archivoFactura( Pedido pedido ) {
        File f = new File( CARPETA_FACTURAS + PREFIJO_FACTURAS + pedido.getIdPedido( ) + ".txt" );
        facturasCreadas.add( f );
        return f;
    }

    private String leer( File archivo ) throws IOException  {
        return new String( Files.readAllBytes( archivo.toPath( ) ), Charset.defaultCharset( ) );
    }

//restaurante vacio
    @Test
    void testRestauranteNuevoEstaVacio( ) {
        assertNull( restaurante.getPedidoEnCurso( ), "Un restaurante nuevo no tiene pedido en curso." );
        assertNotNull( restaurante.getPedidos( ), "La lista de pedidos no debe ser null." );
        assertNotNull( restaurante.getIngredientes( ), "La lista de ingredientes no debe ser null." );
        assertNotNull( restaurante.getMenuBase( ), "El menú base no debe ser null." );
        assertNotNull( restaurante.getMenuCombos( ), "El menú de combos no debe ser null." );
        assertEquals( 0, restaurante.getPedidos( ).size( ), "No debe haber pedidos." );
        assertEquals( 0, restaurante.getIngredientes( ).size( ), "No debe haber ingredientes." );
        assertEquals( 0, restaurante.getMenuBase( ).size( ), "No debe haber productos en el menú." );
        assertEquals( 0, restaurante.getMenuCombos( ).size( ), "No debe haber combos." );
    }

//cargar información
    @Test
    void testCargarIngredientes( ) throws Exception {
        cargarArchivosOk( );

        ArrayList<Ingrediente> ingredientes = restaurante.getIngredientes( );
        assertEquals( 3, ingredientes.size( ), "La línea vacía debe ignorarse y quedar 3 ingredientes." );
        assertEquals( "lechuga", ingredientes.get( 0 ).getNombre( ), "Primer ingrediente incorrecto." );
        assertEquals( 1000, ingredientes.get( 0 ).getCostoAdicional( ), "Costo del primer ingrediente incorrecto." );
        assertEquals( "tomate", ingredientes.get( 1 ).getNombre( ), "Segundo ingrediente incorrecto." );
        assertEquals( 1000, ingredientes.get( 1 ).getCostoAdicional( ), "Costo del segundo ingrediente incorrecto." );
        assertEquals( "queso mozzarella", ingredientes.get( 2 ).getNombre( ), "Tercer ingrediente incorrecto." );
        assertEquals( 2500, ingredientes.get( 2 ).getCostoAdicional( ), "Costo del tercer ingrediente incorrecto." );
    }

    @Test
    void testCargarMenuBase( ) throws Exception {
        cargarArchivosOk( );

        ArrayList<ProductoMenu> menu = restaurante.getMenuBase( );
        assertEquals( 3, menu.size( ), "La línea vacía debe ignorarse y quedar 3 productos." );
        assertEquals( "corral", menu.get( 0 ).getNombre( ), "Primer producto incorrecto." );
        assertEquals( 14000, menu.get( 0 ).getPrecio( ), "Precio del primer producto incorrecto." );
        assertEquals( "papas medianas", menu.get( 1 ).getNombre( ), "Segundo producto incorrecto." );
        assertEquals( 5500, menu.get( 1 ).getPrecio( ), "Precio del segundo producto incorrecto." );
        assertEquals( "gaseosa", menu.get( 2 ).getNombre( ), "Tercer producto incorrecto." );
        assertEquals( 5000, menu.get( 2 ).getPrecio( ), "Precio del tercer producto incorrecto." );
    }

    @Test
    void testCargarCombos( ) throws Exception {
        cargarArchivosOk( );

        ArrayList<Combo> combos = restaurante.getMenuCombos( );
        assertEquals( 2, combos.size( ), "La línea vacía debe ignorarse y quedar 2 combos." );

        Combo corral = combos.get( 0 );
        assertEquals( "combo corral", corral.getNombre( ), "Nombre del primer combo incorrecto." );
        // corral + papas medianas + gaseosa = 24500, con 10% de descuento
        assertEquals( 22050, corral.getPrecio( ), "Precio del primer combo incorrecto (¿se aplicó bien el descuento?)." );

        Combo mitad = combos.get( 1 );
        assertEquals( "combo mitad", mitad.getNombre( ), "Nombre del segundo combo incorrecto." );
        // corral + gaseosa = 19000, con 50% de descuento
        assertEquals( 9500, mitad.getPrecio( ), "Precio del segundo combo incorrecto." );
    }

    @Test
    void testCargarArchivosRealesDelRestaurante( ) throws Exception {
        restaurante.cargarInformacionRestaurante( new File( "data/ingredientes.txt" ), new File( "data/menu.txt" ), new File( "data/combos.txt" ) );

        assertEquals( 15, restaurante.getIngredientes( ).size( ), "Cantidad de ingredientes incorrecta." );
        assertEquals( 22, restaurante.getMenuBase( ).size( ), "Cantidad de productos del menú incorrecta." );
        assertEquals( 4, restaurante.getMenuCombos( ).size( ), "Cantidad de combos incorrecta." );

        // combo especial: especial (24000) + papas medianas (5500) + gaseosa (5000) = 34500, con 9.5% de descuento = 31222.5
        Combo especial = null;
        for( Combo c : restaurante.getMenuCombos( ) ) {
            if( c.getNombre( ).equals( "combo especial" ) )
                especial = c;
        }
        assertNotNull( especial, "Debe existir el combo especial." );
        assertEquals( 31222, especial.getPrecio( ), "Precio del combo especial incorrecto." );
    }

    @Test
    void testCargarIngredientesRepetidos( )  {
        IngredienteRepetidoException e = assertThrows( IngredienteRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( archivo( "ingredientes_repetido.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_ok.txt" ) ),
                "Debe lanzar IngredienteRepetidoException." );
        assertEquals( "El ingrediente lechuga está repetido", e.getMessage( ), "Mensaje de la excepción incorrecto." );
    }

    @Test
    void testCargarMenuRepetido( ) {
        ProductoRepetidoException e = assertThrows( ProductoRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), archivo( "menu_repetido.txt" ), archivo( "combos_ok.txt" ) ),
                "Debe lanzar ProductoRepetidoException." );
        assertEquals( "El producto corral está repetido", e.getMessage( ), "Mensaje de la excepción incorrecto." );
    }

    @Test
    void testCargarCombosRepetidos( ) {
        ProductoRepetidoException e = assertThrows( ProductoRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_repetido.txt" ) ),
                "Debe lanzar ProductoRepetidoException." );
        assertEquals( "El producto combo corral está repetido", e.getMessage( ), "Mensaje de la excepción incorrecto." );
    }

    @Test
    void testCargarComboConProductoFaltante( ) {
        ProductoFaltanteException e = assertThrows( ProductoFaltanteException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_faltante.txt" ) ),
                "Debe lanzar ProductoFaltanteException." );
        assertEquals( "El producto hamburguesa inexistente no aparece en la información del restaurante", e.getMessage( ), "Mensaje de la excepción incorrecto." );
    }

    @Test
    void testCargarArchivosInexistentes( ) {
        File noExiste = archivo( "no_existe.txt" );

        // Se usa un restaurante nuevo en cada caso para que no haya datos cargados de la llamada anterior
        assertThrows( IOException.class, ( ) -> new Restaurante( ).cargarInformacionRestaurante( noExiste, archivo( "menu_ok.txt" ), archivo( "combos_ok.txt" ) ),
                "Debe lanzar IOException si no existe el archivo de ingredientes." );
        assertThrows( IOException.class, ( ) -> new Restaurante( ).cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), noExiste, archivo( "combos_ok.txt" ) ),
                "Debe lanzar IOException si no existe el archivo del menú." );
        assertThrows( IOException.class, ( ) -> new Restaurante( ).cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), archivo( "menu_ok.txt" ), noExiste ),
                "Debe lanzar IOException si no existe el archivo de combos." );
    }

    @Test
    void testCargarArchivosConNumerosInvalidos( ) {
        assertThrows( NumberFormatException.class,
                ( ) -> new Restaurante( ).cargarInformacionRestaurante( archivo( "ingredientes_invalido.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_ok.txt" ) ),
                "Costo de ingrediente inválido." );
        assertThrows( NumberFormatException.class,
                ( ) -> new Restaurante( ).cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), archivo( "menu_invalido.txt" ), archivo( "combos_ok.txt" ) ),
                "Precio de producto inválido." );
        assertThrows( NumberFormatException.class,
                ( ) -> new Restaurante( ).cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_invalido.txt" ) ),
                "Descuento de combo inválido." );
    }

    @Test
    void testCargarInformacionDosVecesLanzaExcepcion( ) throws Exception {
        cargarArchivosOk( );
        assertThrows( HamburguesaException.class, ( ) -> cargarArchivosOk( ), "Cargar de nuevo los mismos datos debe detectar elementos repetidos." );
    }


//un periodo
    @Test
    void testIniciarPedido( ) throws Exception {
        restaurante.iniciarPedido( "Andrea Pérez", "Calle 1 # 46-3" );

        Pedido pedido = restaurante.getPedidoEnCurso( );
        assertNotNull( pedido, "Debe haber un pedido en curso." );
        assertEquals( "Andrea Pérez", pedido.getNombreCliente( ), "El nombre del cliente no es el esperado." );
        assertEquals( 0, restaurante.getPedidos( ).size( ), "Un pedido en curso todavía no hace parte de los pedidos cerrados." );
    }

    @Test
    void testIniciarPedidoYaHayUnoEnCurso( ) throws Exception {
        restaurante.iniciarPedido( "Andrea Pérez", "Calle 1 # 46-3" );
        Pedido primero = restaurante.getPedidoEnCurso( );

        YaHayUnPedidoEnCursoException e = assertThrows( YaHayUnPedidoEnCursoException.class, ( ) -> restaurante.iniciarPedido( "Luis", "Carrera 5" ),
                "Debe lanzar YaHayUnPedidoEnCursoException." );

        assertEquals( "Ya existe un pedido en curso, para el cliente Andrea Pérez así que no se puede crear un pedido para Luis", e.getMessage( ), "Mensaje de la excepción incorrecto." );
        assertSame( primero, restaurante.getPedidoEnCurso( ), "El pedido en curso no debe cambiar." );
        assertEquals( "Andrea Pérez", restaurante.getPedidoEnCurso( ).getNombreCliente( ), "El pedido en curso no debe cambiar." );
    }

    @Test
    void testCerrarYGuardarPedidoSinPedidoEnCurso( ) {
        NoHayPedidoEnCursoException e = assertThrows( NoHayPedidoEnCursoException.class, ( ) -> restaurante.cerrarYGuardarPedido( ), "Debe lanzar NoHayPedidoEnCursoException." );
        assertEquals( "Actualmente no hay un pedido en curso", e.getMessage( ), "Mensaje de la excepción incorrecto." );
    }

    @Test
    void testCerrarYGuardarPedido( ) throws Exception {
        cargarArchivosOk( );
        restaurante.iniciarPedido( "Andrea Pérez", "Calle 1 # 46-3" );
        Pedido pedido = restaurante.getPedidoEnCurso( );
        pedido.agregarProducto( restaurante.getMenuBase( ).get( 0 ) );
        pedido.agregarProducto( restaurante.getMenuCombos( ).get( 0 ) );
        File factura = archivoFactura( pedido );
        String textoEsperado = pedido.generarTextoFactura( );

        restaurante.cerrarYGuardarPedido( );

        assertNull( restaurante.getPedidoEnCurso( ), "Después de cerrar no debe haber pedido en curso." );
        assertEquals( 1, restaurante.getPedidos( ).size( ), "El pedido cerrado debe quedar en el histórico." );
        assertSame( pedido, restaurante.getPedidos( ).get( 0 ), "El pedido guardado debe ser el que se cerró." );
        assertTrue( factura.exists( ), "Debe existir el archivo " + factura.getPath( ) );
        assertEquals( textoEsperado, leer( factura ), "El archivo debe contener el texto de la factura del pedido." );
    }

    @Test
    void testCerrarPedidoVacio( ) throws Exception {
        restaurante.iniciarPedido( "Andrea Pérez", "Calle 1 # 46-3" );
        Pedido pedido = restaurante.getPedidoEnCurso( );
        File factura = archivoFactura( pedido );

        restaurante.cerrarYGuardarPedido( );

        assertTrue( factura.exists( ), "Debe existir el archivo de la factura." );
        assertEquals( pedido.generarTextoFactura( ), leer( factura ), "El archivo debe contener el texto de la factura." );
        assertEquals( 1, restaurante.getPedidos( ).size( ), "El pedido cerrado debe quedar en el histórico." );
    }

    @Test
    void testCerrarDosVecesSeguidasLanzaExcepcion( ) throws Exception {
        restaurante.iniciarPedido( "Andrea Pérez", "Calle 1 # 46-3" );
        archivoFactura( restaurante.getPedidoEnCurso( ) );
        restaurante.cerrarYGuardarPedido( );

        assertThrows( NoHayPedidoEnCursoException.class, ( ) -> restaurante.cerrarYGuardarPedido( ), "Ya no hay pedido en curso." );
        assertEquals( 1, restaurante.getPedidos( ).size( ), "No debe duplicarse el pedido en el histórico." );
    }

//varios pedidos
    @Test
    void testVariosPedidosYConsultaPorIdentificador( ) throws Exception {
        cargarArchivosOk( );

        restaurante.iniciarPedido( "Andrea", "Calle 1" );
        Pedido p1 = restaurante.getPedidoEnCurso( );
        p1.agregarProducto( restaurante.getMenuBase( ).get( 0 ) );
        File f1 = archivoFactura( p1 );
        restaurante.cerrarYGuardarPedido( );

        restaurante.iniciarPedido( "Luis", "Carrera 2" );
        Pedido p2 = restaurante.getPedidoEnCurso( );
        p2.agregarProducto( restaurante.getMenuBase( ).get( 1 ) );
        File f2 = archivoFactura( p2 );
        restaurante.cerrarYGuardarPedido( );

        ArrayList<Pedido> pedidos = restaurante.getPedidos( );
        assertEquals( 2, pedidos.size( ), "Deben haber dos pedidos cerrados." );
        assertTrue( p1.getIdPedido( ) != p2.getIdPedido( ), "Los pedidos deben tener identificadores distintos." );

        Pedido consultado = null;
        for( Pedido p : pedidos ) {
            if( p.getIdPedido( ) == p2.getIdPedido( ) )
                consultado = p;
        }
        assertSame( p2, consultado, "Debe poder consultarse un pedido por su identificador." );

        assertEquals( p1.generarTextoFactura( ), leer( f1 ), "Factura del primer pedido incorrecta." );
        assertEquals( p2.generarTextoFactura( ), leer( f2 ), "Factura del segundo pedido incorrecta." );
    }

}
