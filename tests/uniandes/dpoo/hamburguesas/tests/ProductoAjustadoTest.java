package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoAjustadoTest {
	private ProductoMenu base;
    private ProductoAjustado ajustado;
    private Ingrediente queso;
    private Ingrediente huevo;
    private Ingrediente tomate;

    @BeforeEach
    void setUp( ) throws Exception {
        base = new ProductoMenu( "corral", 14000 );
        ajustado = new ProductoAjustado( base );
        queso = new Ingrediente( "queso mozzarella", 2500 );
        huevo = new Ingrediente( "huevo", 3000 );
        tomate = new Ingrediente( "tomate", 1000 );
    }

    @AfterEach
    void tearDown( ) throws Exception {
    }


    @SuppressWarnings( "unchecked" )
    private void ajustar( String lista, Ingrediente ingrediente ) throws Exception {
        Field campo = ProductoAjustado.class.getDeclaredField( lista );
        campo.setAccessible( true );
        ( (ArrayList<Ingrediente>) campo.get( ajustado ) ).add( ingrediente );
    }

    @Test
    void testGetNombre( ) {
        assertEquals( "corral", ajustado.getNombre( ), "El nombre debe ser el del producto base." );
    }

    @Test
    void testGetPrecioSinAjustes( ) {
        assertEquals( 14000, ajustado.getPrecio( ), "Sin ajustes el precio debe ser el del producto base." );
    }

    @Test
    void testGetPrecioConUnAgregado( ) throws Exception {
        ajustar( "agregados", queso );
        assertEquals( 16500, ajustado.getPrecio( ), "El precio debe ser el del base más el ingrediente agregado." );
    }

    @Test
    void testGetPrecioConVariosAgregados( ) throws Exception {
        ajustar( "agregados", queso );
        ajustar( "agregados", huevo );
        ajustar( "agregados", queso );
        assertEquals( 14000 + 2500 + 3000 + 2500, ajustado.getPrecio( ), "El mismo ingrediente puede agregarse varias veces y cada vez se cobra." );
    }

    @Test
    void testGetPrecioConEliminadosNoReduce( ) throws Exception {
        ajustar( "eliminados", tomate );
        ajustar( "eliminados", queso );
        assertEquals( 14000, ajustado.getPrecio( ), "Quitar ingredientes no debe reducir el precio." );
    }

    @Test
    void testGetPrecioConAgregadosYEliminados( ) throws Exception {
        ajustar( "agregados", huevo );
        ajustar( "eliminados", tomate );
        assertEquals( 17000, ajustado.getPrecio( ), "Solo los agregados deben sumar al precio." );
    }

    @Test
    void testPrecioSeActualizaAlAjustar( ) throws Exception {
        assertEquals( 14000, ajustado.getPrecio( ), "Precio inicial incorrecto." );
        ajustar( "agregados", queso );
        assertEquals( 16500, ajustado.getPrecio( ), "El precio debe recalcularse al agregar un ingrediente." );
    }

    @Test
    void testProductoBaseNoSeModifica( ) throws Exception {
        ajustar( "agregados", queso );
        assertEquals( 14000, base.getPrecio( ), "El ajuste no debe modificar el precio del producto base." );
        assertEquals( "corral", base.getNombre( ), "El ajuste no debe modificar el nombre del producto base." );
    }
    
    @Test
    void testGenerarTextoFacturaSinAjustes( )
    {
        String esperado = "corral\n" + "            14000\n";
        assertEquals( esperado, ajustado.generarTextoFactura( ), "La factura sin ajustes no es la esperada." );
    }

    @Test
    void testGenerarTextoFacturaConAgregados( ) throws Exception
    {
        ajustar( "agregados", queso );
        ajustar( "agregados", huevo );

        String esperado = "corral\n" + "    +queso mozzarella                2500\n" + "    +huevo                3000\n" + "            19500\n";
        assertEquals( esperado, ajustado.generarTextoFactura( ), "La factura con ingredientes agregados no es la esperada." );
    }
    
    @Test
    void testGenerarTextoFacturaConEliminados( ) throws Exception
    {
        ajustar( "eliminados", tomate );

        String esperado = "corral\n" + "    -tomate\n" + "            14000\n";
        assertEquals( esperado, ajustado.generarTextoFactura( ), "La factura con ingredientes eliminados no es la esperada." );
    }

    @Test
    void testGenerarTextoFacturaConAgregadosYEliminados( ) throws Exception
    {
        ajustar( "agregados", queso );
        ajustar( "eliminados", tomate );

        String esperado = "corral\n" + "    +queso mozzarella                2500\n" + "    -tomate\n" + "            16500\n";
        assertEquals( esperado, ajustado.generarTextoFactura( ), "La factura con agregados y eliminados no es la esperada." );
    }

}
