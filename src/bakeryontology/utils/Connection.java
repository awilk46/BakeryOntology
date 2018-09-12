package bakeryontology.utils;

import bakeryontology.entities.Baking;
import bakeryontology.entities.Person;
import bakeryontology.entities.Transaction;
import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

/**
 * @author Adrian Wilk
 * @since 15.05.2017
 */

public class Connection {
    

    //Lists in which objects are stored in tables
    public ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    public ObservableList<Baking> bakingList = FXCollections.observableArrayList();
    public ObservableList<Person> personList = FXCollections.observableArrayList();
    
    //The path to the file on which it works
    public static File mainFile = new File("./ontology/Bakery.owl");
    
    //The file in which it holds the temporary data, needed to update the ontology
    public static File temporaryData;
    
    //The ontology name, extracted from protegee
    private static final String BASE_URL = "http://www.semanticweb.org/adria/ontologies/2017/5/bakery";
    
    //Tool for parsing double values
    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
    
    //Tools for working on ontology
    private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
    private OWLOntologyManager manager = null;
    private OWLOntology ontology = null;
    private OWLReasonerFactory reasonerFactory;
    private OWLReasoner reasoner;
    private OWLDataFactory factory;
    private PrefixOWLOntologyFormat pm;

    /**
     * Opens connection with the ontology, 
     * it initializes the fields needed for oncology operations
     **/
    public boolean openConnection(File file) {

        //Open the ontology documentation from the protegee page

        manager = OWLManager.createOWLOntologyManager();

        try {
            ontology = manager.loadOntologyFromOntologyDocument(IRI.create(file));
        } catch (OWLOntologyCreationException e) {
            return false;
        }

        reasonerFactory = new StructuralReasonerFactory();
        reasoner = reasonerFactory.createReasoner(ontology);

        factory = manager.getOWLDataFactory();
        pm = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
        pm.setDefaultPrefix(BASE_URL + "#");
        return true;
    }
    
    //Saves the changes it made in the ontology manager to the owl file
    public boolean saveConnection(File file) {
        IRI documentIRI2;
        if (file == null) {
            documentIRI2 = IRI.create(mainFile);
        } else {
            documentIRI2 = IRI.create(file);
        }
        try {
            manager.saveOntology(ontology, new OWLXMLOntologyFormat(), documentIRI2);
        } catch (OWLOntologyStorageException ex) {
            return false;
        }
        return true;
    }
    
     public void updateTransactionsForTable() {
         
        //Class from which it extracts data
        OWLClass transactionClass = factory.getOWLClass(":Transaction", pm);
        
        //The property it wants to draw
        OWLDataProperty costProperty = factory.getOWLDataProperty(":Cost", pm);
        
        //Interdependence between classes
        OWLObjectProperty bakingProperty = factory.getOWLObjectProperty(":SoldBaking", pm);
        OWLObjectProperty clientProperty = factory.getOWLObjectProperty(":Client", pm);
        OWLObjectProperty employeeProperty = factory.getOWLObjectProperty(":Employee", pm);
        
        for (OWLNamedIndividual a : reasoner.getInstances(transactionClass, false).getFlattened()) {
            
             //Clean java model that accuses instances in Protegee ontology
            Transaction model = new Transaction();
            
            //Sets the object ID, in the ontology is its name
            model.setId(renderer.render(a));
            
            //Pulls out a particular model knowing its name from the loop
            OWLNamedIndividual transaction = factory.getOWLNamedIndividual(":" + renderer.render(a), pm);
            
            //Extract all properties from the ontology

            //Cost property
            reasoner.getDataPropertyValues(transaction, costProperty).iterator().forEachRemaining(n -> {
                try {
                    Number number = format.parse(n.getLiteral());
                    model.setCost(number.doubleValue());
                } catch (ParseException ex) {
                    System.out.println(ex);
                }
            });
            
            //Baking property
            reasoner.getObjectPropertyValues(transaction, bakingProperty).getFlattened().forEach(n -> {
                model.setBaking(renderer.render(n));
            });
            //Employee property
            reasoner.getObjectPropertyValues(transaction, employeeProperty).getFlattened().forEach(n -> {
                model.setEmployee(renderer.render(n));
            });
            //Client property
            reasoner.getObjectPropertyValues(transaction, clientProperty).getFlattened().forEach(n -> {
                model.setClient(renderer.render(n));
            });
            
            //The filled model is thrown into the collection
            transactionList.add(model);

        }
     }
     
     public void updateBakingForTable() {
         

        OWLClass breadClass = factory.getOWLClass(":Bread", pm);
        OWLClass rollClass = factory.getOWLClass(":Roll", pm);
        
        OWLDataProperty idProperty = factory.getOWLDataProperty(":BakingID", pm);
        OWLDataProperty validityProperty = factory.getOWLDataProperty(":ValidityDays", pm);
        
        for (OWLNamedIndividual a : reasoner.getInstances(breadClass, false).getFlattened()) {
            
            Baking model = new Baking();
            
            model.setName(renderer.render(a));
            
            model.setType("Bread");
            
            OWLNamedIndividual breadBaking = factory.getOWLNamedIndividual(":" + renderer.render(a), pm);

            reasoner.getDataPropertyValues(breadBaking, idProperty).iterator().forEachRemaining(n -> {
                model.setId(Integer.parseInt(n.getLiteral()));
            });
            
            reasoner.getDataPropertyValues(breadBaking, validityProperty).iterator().forEachRemaining(n -> {
                model.setValidity(Integer.parseInt(n.getLiteral()));
            });

            bakingList.add(model);
        }
        
        for (OWLNamedIndividual a : reasoner.getInstances(rollClass, false).getFlattened()) {
            
            Baking model = new Baking();

            model.setName(renderer.render(a));

            model.setType("Roll");
            
            OWLNamedIndividual rollBaking = factory.getOWLNamedIndividual(":" + renderer.render(a), pm);
            
            reasoner.getDataPropertyValues(rollBaking, idProperty).iterator().forEachRemaining(n -> {
                model.setId(Integer.parseInt(n.getLiteral()));
            });

             reasoner.getDataPropertyValues(rollBaking, validityProperty).iterator().forEachRemaining(n -> {
                model.setValidity(Integer.parseInt(n.getLiteral()));
            });
            bakingList.add(model);
        }
     }
     
     public void updatePersonsForTable() {
         
        OWLClass clientClass = factory.getOWLClass(":Client", pm);
        OWLClass employeeClass = factory.getOWLClass(":Employee", pm);

        OWLDataProperty idProperty = factory.getOWLDataProperty(":PersonID", pm);
        OWLDataProperty fullNameProperty = factory.getOWLDataProperty(":FullName", pm);
        
        for (OWLNamedIndividual a : reasoner.getInstances(clientClass, false).getFlattened()) {
            
            Person model = new Person();

            model.setFullName(renderer.render(a));

            model.setType("Client");

            OWLNamedIndividual clientPerson = factory.getOWLNamedIndividual(":" + renderer.render(a), pm);
            
            reasoner.getDataPropertyValues(clientPerson, idProperty).iterator().forEachRemaining(n -> {
                model.setId(Integer.parseInt(n.getLiteral()));
            });
            
            reasoner.getDataPropertyValues(clientPerson, fullNameProperty).iterator().forEachRemaining(n -> {
                model.setFullName(n.getLiteral());
            });

            personList.add(model);        
        }
        
        for (OWLNamedIndividual a : reasoner.getInstances(employeeClass, false).getFlattened()) {
            
            Person model = new Person();

            model.setFullName(renderer.render(a));

            model.setType("Employee");

            OWLNamedIndividual employeePerson = factory.getOWLNamedIndividual(":" + renderer.render(a), pm);
            
            reasoner.getDataPropertyValues(employeePerson, idProperty).iterator().forEachRemaining(n -> {
                model.setId(Integer.parseInt(n.getLiteral()));
            });
            
            reasoner.getDataPropertyValues(employeePerson, fullNameProperty).iterator().forEachRemaining(n -> {
                model.setFullName(n.getLiteral());
            });

            personList.add(model);
        }      
     }
     
    //Returns a list of Data to be thrown into the ComboBox
    public String[] getBakingList() {

        ArrayList<String> list = new ArrayList<>();

        OWLClass breadClass = factory.getOWLClass(":Bread", pm);
        OWLClass rollClass = factory.getOWLClass(":Roll", pm);

        for (OWLNamedIndividual a : reasoner.getInstances(breadClass, false).getFlattened()) {
            list.add(renderer.render(a));
        }
        for (OWLNamedIndividual a : reasoner.getInstances(rollClass, false).getFlattened()) {
            list.add(renderer.render(a));
        }
        return list.toArray(new String[0]);
    }
         
    public String[] getEmployeeList() {

        ArrayList<String> list = new ArrayList<>();

        OWLClass employeeClass = factory.getOWLClass(":Employee", pm);

        for (OWLNamedIndividual a : reasoner.getInstances(employeeClass, false).getFlattened()) {
            list.add(renderer.render(a));
        }
        return list.toArray(new String[0]);
    }
    
    public String[] getClientList() {

        //pusta lista do zwrócenia
        ArrayList<String> list = new ArrayList<>();

        //klasa z jakiej wyciągam dane
        OWLClass clientClass = factory.getOWLClass(":Client", pm);

        for (OWLNamedIndividual a : reasoner.getInstances(clientClass, false).getFlattened()) {
            list.add(renderer.render(a));
        }
        return list.toArray(new String[0]);
    }
     
    //Adds new person
    public void addPerson(Person model) {
        
        //Selects subclass
        OWLClass personClass;
        if (model.getType().equals("Client")) {
            personClass = factory.getOWLClass(":Client", pm);
        } else {
            personClass = factory.getOWLClass(":Employee", pm);
        }
        //Adds new individual to the ontology
        OWLNamedIndividual person = new OWLNamedIndividualImpl(IRI.create("#" + model.getFullName()));
        OWLClassAssertionAxiom ax0 = factory.getOWLClassAssertionAxiom(personClass, person);
        manager.addAxiom(ontology, ax0);
        
        //The type of data needed to add the Integer data property
        OWLDatatype integerDatatype = factory.getOWLDatatype(OWL2Datatype.XSD_INTEGER
                .getIRI());
        
        //The type of data needed to add the String data property
        OWLDatatype stringDatatype = factory.getOWLDatatype(OWL2Datatype.XSD_STRING
                .getIRI());
        
        //The property to which it will throw data
        OWLDataPropertyExpression idProperty = factory.getOWLDataProperty(":PersonID", pm);
        OWLDataPropertyExpression fullNameProperty = factory.getOWLDataProperty(":FullName", pm);
        
        OWLLiteral idLitereal = factory.getOWLLiteral(String.valueOf(model.getId()), integerDatatype);
        OWLAxiom ax1 = factory.getOWLDataPropertyAssertionAxiom(idProperty, person, idLitereal);
        manager.addAxiom(ontology, ax1);
        
        OWLLiteral nameLiteral = factory.getOWLLiteral(model.getFullName(), stringDatatype);
        OWLAxiom ax2 = factory.getOWLDataPropertyAssertionAxiom(fullNameProperty, person, nameLiteral);
        manager.addAxiom(ontology, ax2);

        //Actualization data
        applyChanges();
    }
    
    public void addBaking(Baking model) {
        
        OWLClass bakingClass;
        if (model.getType().equals("Bread")) {
            bakingClass = factory.getOWLClass(":Bread", pm);
        } else {
            bakingClass = factory.getOWLClass(":Roll", pm);
        }
        
        OWLNamedIndividual baking = new OWLNamedIndividualImpl(IRI.create("#" + model.getName()));
        OWLClassAssertionAxiom ax0 = factory.getOWLClassAssertionAxiom(bakingClass, baking);
        manager.addAxiom(ontology, ax0);
        
        OWLDatatype integerDatatype = factory.getOWLDatatype(OWL2Datatype.XSD_INTEGER
                .getIRI());
        
        OWLDataPropertyExpression idProperty = factory.getOWLDataProperty(":BakingID", pm);
        OWLDataPropertyExpression validityProperty = factory.getOWLDataProperty(":ValidityDays", pm);
        
        OWLLiteral idLitereal = factory.getOWLLiteral(String.valueOf(model.getId()), integerDatatype);
        OWLAxiom ax1 = factory.getOWLDataPropertyAssertionAxiom(idProperty, baking, idLitereal);
        manager.addAxiom(ontology, ax1);
        
        OWLLiteral nameLiteral = factory.getOWLLiteral(String.valueOf(model.getValidity()), integerDatatype);
        OWLAxiom ax2 = factory.getOWLDataPropertyAssertionAxiom(validityProperty, baking, nameLiteral);
        manager.addAxiom(ontology, ax2);

        applyChanges();   
    }
    
    public void addTransaction(Transaction model) {
        
        OWLClass transactionClass = factory.getOWLClass(":Transaction", pm);

        OWLNamedIndividual transaction = new OWLNamedIndividualImpl(IRI.create("#" + model.getId()));
        OWLClassAssertionAxiom ax0 = factory.getOWLClassAssertionAxiom(transactionClass, transaction);
        manager.addAxiom(ontology, ax0);
        
        OWLDatatype doubleDatatype = factory.getOWLDatatype(OWL2Datatype.XSD_DOUBLE
                .getIRI());
        
        OWLDataPropertyExpression costProperty = factory.getOWLDataProperty(":Cost", pm);

         OWLLiteral costLitereal = factory.getOWLLiteral(
                String.valueOf(model.getCost()).substring(0, String.valueOf(model.getCost()).indexOf("."))
                + ","
                + String.valueOf(model.getCost()).substring(String.valueOf(model.getCost()).indexOf(".") + 1,
                        String.valueOf(model.getCost()).length()),
                doubleDatatype);
         
        OWLAxiom ax1 = factory.getOWLDataPropertyAssertionAxiom(costProperty, transaction, costLitereal);
        manager.addAxiom(ontology, ax1);
        
        OWLObjectProperty bakingProperty = factory.getOWLObjectProperty(":SoldBaking", pm);
        OWLObjectProperty employeeProperty = factory.getOWLObjectProperty(":Employee", pm);
        OWLObjectProperty clientProperty = factory.getOWLObjectProperty(":Client", pm);
        
        //An individual with whom It will connect object properties
        OWLNamedIndividual baking = factory.getOWLNamedIndividual(":" + model.getBaking(), pm);
        OWLNamedIndividual client = factory.getOWLNamedIndividual(":" + model.getClient(), pm);
        OWLNamedIndividual employee = factory.getOWLNamedIndividual(":" + model.getEmployee(), pm);
        
        //Connection
        OWLObjectPropertyAssertionAxiom ax2 = factory
                .getOWLObjectPropertyAssertionAxiom(bakingProperty, transaction, baking);
        
        //Add
        manager.addAxiom(ontology, ax2);
        
         OWLObjectPropertyAssertionAxiom ax3 = factory
                .getOWLObjectPropertyAssertionAxiom(clientProperty, transaction, client);
        manager.addAxiom(ontology, ax3);
        
         OWLObjectPropertyAssertionAxiom ax4 = factory
                .getOWLObjectPropertyAssertionAxiom(employeeProperty, transaction, employee);
        manager.addAxiom(ontology, ax4);
        
        applyChanges();
    }
    
    //Delete entity from the ontology
     public void deleteEntity(String name) {  
        OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(":" + name, pm);
        individual.accept(remover);
        manager.applyChanges(remover.getChanges());
        applyChanges();
    }
     
    //Creates new temporary file to which changes are made to the reload time of the ontology tools
    public void applyChanges() { 
        temporaryData = new File("./ontology/tempData.owl");
        saveConnection(temporaryData);
        openConnection(temporaryData); 
    }

}
