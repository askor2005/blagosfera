package ru.radom.blagosferabp.activiti.component.stencil.defaultset.properties;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.stencil.exchange.Property;

import java.util.Arrays;

/**
 * Created by alex on 01.10.2015.<br/>
 * Набор стандартных свойств компонентов
 */
@Getter
@Accessors(fluent = true)
@Component
public class DefaultProperties {

    //TODO переименовать всё

    private final Property process_idpackage = Property.builder()
            .id("process_id")
            .type(Property.STRING)
            .title("Process identifier")
            .value("process")
            .description("Unique identifier of the process definition.")
            .popular(true)
            .build();

    private final Property overrideidpackage = Property.builder()
            .id("overrideid")
            .type(Property.STRING)
            .title("Id")
            .value("")
            .description("Unique identifier of the element.")
            .popular(true)
            .build();

    private final Property namepackage = Property.builder()
            .id("name")
            .type(Property.STRING)
            .title("Name")
            .value("")
            .description("The descriptive name of the BPMN element.")
            .popular(true)
            .refToView(Arrays.asList("text_name"))
            .build();

    private final Property documentationpackage = Property.builder()
            .id("documentation")
            .type(Property.TEXT)
            .title("Documentation")
            .value("")
            .description("The descriptive name of the BPMN element.")
            .popular(true)
            .build();

    private final Property process_authorpackage = Property.builder()
            .id("process_author")
            .type(Property.STRING)
            .title("Process author")
            .value("")
            .description("Author of the process definition.")
            .popular(true)
            .build();

    private final Property process_versionpackage = Property.builder()
            .id("process_version")
            .type(Property.STRING)
            .title("Process version string (documentation only)")
            .value("")
            .description("Version identifier for documentation purpose.")
            .popular(true)
            .build();

    private final Property process_namespacepackage = Property.builder()
            .id("process_namespace")
            .type(Property.STRING)
            .title("Target namespace")
            .value("http://www.activiti.org/processdef")
            .description("Target namespace for the process definition.")
            .popular(true)
            .build();

    private final Property asynchronousdefinitionpackage = Property.builder()
            .id("asynchronousdefinition")
            .type(Property.BOOLEAN)
            .title("Asynchronous")
            .value(false)
            .description("Define the activity as asynchronous.")
            .popular(true)
            .build();

    private final Property exclusivedefinitionpackage = Property.builder()
            .id("exclusivedefinition")
            .type(Property.BOOLEAN)
            .title("Exclusive")
            .value(false)
            .description("Define the activity as exclusive.")
            .popular(true)
            .build();

    private final Property executionlistenerspackage = Property.builder()
            .id("executionlisteners")
            .type(Property.MULTIPLECOMPLEX)
            .title("Execution listeners")
            .value("")
            .description("Listeners for an activity, process, sequence flow, start and end event.")
            .popular(true)
            .build();

    private final Property tasklistenerspackage = Property.builder()
            .id("tasklisteners")
            .type(Property.MULTIPLECOMPLEX)
            .title("Task listeners")
            .value("")
            .description("Listeners for a user task")
            .popular(true)
            .build();

    private final Property eventlistenerspackage = Property.builder()
            .id("eventlisteners")
            .type(Property.MULTIPLECOMPLEX)
            .title("Event listeners")
            .value("")
            .description("Listeners for any event happening in the Activiti Engine. It's also possible to rethrow the event as a signal, message or error event")
            .popular(true)
            .build();

    private final Property usertaskassignmentpackage = Property.builder()
            .id("usertaskassignment")
            .type(Property.COMPLEX)
            .title("Assignments")
            .value("")
            .description("Assignment definition for the user task")
            .popular(true)
            .build();

    private final Property formpropertiespackage = Property.builder()
            .id("formproperties")
            .type(Property.COMPLEX)
            .title("Form properties")
            .value("")
            .description("Definition of the form with a list of form properties")
            .popular(true)
            .build();

    private final Property formkeydefinitionpackage = Property.builder()
            .id("formkeydefinition")
            .type(Property.STRING)
            .title("Form key")
            .value("")
            .description("Form key that provides a reference to a form.")
            .popular(true)
            .build();

    private final Property prioritydefinitionpackage = Property.builder()
            .id("prioritydefinition")
            .type(Property.STRING)
            .title("Priority")
            .value("")
            .description("Priority of the user task.")
            .popular(true)
            .build();

    private final Property duedatedefinitionpackage = Property.builder()
            .id("duedatedefinition")
            .type(Property.STRING)
            .title("Due date")
            .value("")
            .description("Due date of the user task.")
            .popular(true)
            .build();

    private final Property servicetaskclasspackage = Property.builder()
            .id("servicetaskclass")
            .type(Property.STRING)
            .title("Class")
            .value("")
            .description("Class that implements the service task logic.")
            .popular(true)
            .build();

    private final Property servicetaskexpressionpackage = Property.builder()
            .id("servicetaskexpression")
            .type(Property.STRING)
            .title("Expression")
            .value("")
            .description("Service task logic defined with an expression.")
            .popular(true)
            .build();

    private final Property servicetaskdelegateexpressionpackage = Property.builder()
            .id("servicetaskdelegateexpression")
            .type(Property.STRING)
            .title("Delegate expression")
            .value("")
            .description("Service task logic defined with a delegate expression.")
            .popular(true)
            .build();

    private final Property servicetaskfieldspackage = Property.builder()
            .id("servicetaskfields")
            .type(Property.COMPLEX)
            .title("Class fields")
            .value("")
            .description("Field extensions")
            .popular(true)
            .build();

    private final Property servicetaskresultvariablepackage = Property.builder()
            .id("servicetaskresultvariable")
            .type(Property.STRING)
            .title("Result variable name")
            .value("")
            .description("Process variable name to store the service task result.")
            .popular(true)
            .build();

    private final Property scriptformatpackage = Property.builder()
            .id("scriptformat")
            .type(Property.STRING)
            .title("Script format")
            .value("")
            .description("Script format of the script task.")
            .popular(true)
            .build();

    private final Property scripttextpackage = Property.builder()
            .id("scripttext")
            .type(Property.TEXT)
            .title("Script")
            .value("")
            .description("Script text of the script task.")
            .popular(true)
            .build();

    private final Property ruletask_rulespackage = Property.builder()
            .id("ruletask_rules")
            .type(Property.STRING)
            .title("Rules")
            .value("")
            .description("Rules of the rule task.")
            .popular(true)
            .build();

    private final Property ruletask_variables_inputpackage = Property.builder()
            .id("ruletask_variables_input")
            .type(Property.STRING)
            .title("Input variables")
            .value("")
            .description("Input variables of the rule task.")
            .popular(true)
            .build();

    private final Property ruletask_excludepackage = Property.builder()
            .id("ruletask_exclude")
            .type(Property.BOOLEAN)
            .title("Exclude")
            .value(false)
            .description("Use the rules property as exclusion.")
            .popular(true)
            .build();

    private final Property ruletask_resultpackage = Property.builder()
            .id("ruletask_result")
            .type(Property.STRING)
            .title("Result variable")
            .value("")
            .description("Result variable of the rule task.")
            .popular(true)
            .build();

    private final Property mailtasktopackage = Property.builder()
            .id("mailtaskto")
            .type(Property.TEXT)
            .title("To")
            .value("")
            .description("The recipients if the e-mail. Multiple recipients are defined in a comma-separated list.")
            .popular(true)
            .build();

    private final Property mailtaskfrompackage = Property.builder()
            .id("mailtaskfrom")
            .type(Property.TEXT)
            .title("From")
            .value("")
            .description("The sender e-mail address. If not provided, the default configured from address is used.")
            .popular(true)
            .build();

    private final Property mailtasksubjectpackage = Property.builder()
            .id("mailtasksubject")
            .type(Property.TEXT)
            .title("Subject")
            .value("")
            .description("The subject of the e-mail.")
            .popular(true)
            .build();

    private final Property mailtaskccpackage = Property.builder()
            .id("mailtaskcc")
            .type(Property.TEXT)
            .title("Cc")
            .value("")
            .description("The cc's of the e-mail. Multiple recipients are defined in a comma-separated list")
            .popular(true)
            .build();

    private final Property mailtaskbccpackage = Property.builder()
            .id("mailtaskbcc")
            .type(Property.TEXT)
            .title("Bcc")
            .value("")
            .description("The bcc's of the e-mail. Multiple recipients are defined in a comma-separated list")
            .popular(true)
            .build();

    private final Property mailtasktextpackage = Property.builder()
            .id("mailtasktext")
            .type(Property.TEXT)
            .title("Text")
            .value("")
            .description("" +
                            "The content of the e-mail, in case one needs to send plain none-rich e-mails. " +
                            "Can be used in combination with html, for e-mail clients that don't support rich content. " +
                            "The client will then fall back to this text"
            )
            .popular(true)
            .build();

    private final Property mailtaskhtmlpackage = Property.builder()
            .id("mailtaskhtml")
            .type(Property.TEXT)
            .title("Html")
            .value("")
            .description("A piece of HTML that is the content of the e-mail.")
            .popular(true)
            .build();

    private final Property mailtaskcharsetpackage = Property.builder()
            .id("mailtaskcharset")
            .type(Property.STRING)
            .title("Charset")
            .value("")
            .description("Allows to change the charset of the email, which is necessary for many non-English languages. ")
            .popular(true)
            .build();

    private final Property callactivitycalledelementpackage = Property.builder()
            .id("callactivitycalledelement")
            .type(Property.STRING)
            .title("Called element")
            .value("")
            .description("Process reference.")
            .popular(true)
            .build();

    private final Property callactivityinparameterspackage = Property.builder()
            .id("callactivityinparameters")
            .type(Property.COMPLEX)
            .title("In parameters")
            .value("")
            .description("Definition of the input parameters")
            .popular(true)
            .build();

    private final Property callactivityoutparameterspackage = Property.builder()
            .id("callactivityoutparameters")
            .type(Property.COMPLEX)
            .title("Out parameters")
            .value("")
            .description("Definition of the output parameters")
            .popular(true)
            .build();

    private final Property cameltaskcamelcontextpackage = Property.builder()
            .id("cameltaskcamelcontext")
            .type(Property.STRING)
            .title("Camel context")
            .value("")
            .description("An optional camel context definition, if left empty the default is used.")
            .popular(true)
            .build();

    private final Property muletaskendpointurlpackage = Property.builder()
            .id("muletaskendpointurl")
            .type(Property.STRING)
            .title("Endpoint url")
            .value("")
            .description("A required endpoint url to sent the message to Mule.")
            .popular(true)
            .build();

    private final Property muletasklanguagepackage = Property.builder()
            .id("muletasklanguage")
            .type(Property.STRING)
            .title("Language")
            .value("")
            .description("A required definition for the language to resolve the payload expression, like juel.")
            .popular(true)
            .build();

    private final Property muletaskpayloadexpressionpackage = Property.builder()
            .id("muletaskpayloadexpression")
            .type(Property.STRING)
            .title("Payload expression")
            .value("")
            .description("A required definition for the payload of the message sent to Mule.")
            .popular(true)
            .build();

    private final Property muletaskresultvariablepackage = Property.builder()
            .id("muletaskresultvariable")
            .type(Property.STRING)
            .title("Result variable")
            .value("")
            .description("An optional result variable for the payload returned.")
            .popular(true)
            .build();

    private final Property conditionsequenceflowpackage = Property.builder()
            .id("conditionsequenceflow")
            .type(Property.COMPLEX)
            .title("Flow condition")
            .value("")
            .description("The condition of the sequence flow")
            .popular(true)
            .build();

    private final Property defaultflowpackage = Property.builder()
            .id("defaultflow")
            .type(Property.BOOLEAN)
            .title("Default flow")
            .value(false)
            .description("Define the sequence flow as default")
            .popular(true)
            .refToView(Arrays.asList("default"))
            .build();

    private final Property conditionalflowpackage = Property.builder()
            .id("conditionalflow")
            .type(Property.BOOLEAN)
            .title("Conditional flow")
            .value(false)
            .description("Define the sequence flow with a condition")
            .popular(true)
            .build();

    private final Property timercycledefinitionpackage = Property.builder()
            .id("timercycledefinition")
            .type(Property.STRING)
            .title("Time cycle (e.g. R3/PT10H)")
            .value("")
            .description("Define the timer with a ISO-8601 cycle.")
            .popular(true)
            .build();

    private final Property timerdatedefinitionpackage = Property.builder()
            .id("timerdatedefinition")
            .type(Property.STRING)
            .title("Time date in ISO-8601")
            .value("")
            .description("Define the timer with a ISO-8601 date definition.")
            .popular(true)
            .build();

    private final Property timerdurationdefinitionpackage = Property.builder()
            .id("timerdurationdefinition")
            .type(Property.STRING)
            .title("Time duration (e.g. PT5M)")
            .value("")
            .description("Define the timer with a ISO-8601 duration.")
            .popular(true)
            .build();

    private final Property timerenddatedefinitionpackage = Property.builder()
            .id("timerenddatedefinition")
            .type(Property.STRING)
            .title("Time End Date in ISO-8601")
            .value("")
            .description("Define the timer with a ISO-8601 duration.")
            .popular(true)
            .build();

    private final Property messagerefpackage = Property.builder()
            .id("messageref")
            .type(Property.STRING)
            .title("Message reference")
            .value("")
            .description("Define the message name.")
            .popular(true)
            .build();

    private final Property signalrefpackage = Property.builder()
            .id("signalref")
            .type(Property.STRING)
            .title("Signal reference")
            .value("")
            .description("Define the signal name.")
            .popular(true)
            .build();

    private final Property errorrefpackage = Property.builder()
            .id("errorref")
            .type(Property.STRING)
            .title("Error reference")
            .value("")
            .description("Define the error name.")
            .popular(true)
            .build();

    private final Property cancelactivitypackage = Property.builder()
            .id("cancelactivity")
            .type(Property.BOOLEAN)
            .title("Cancel activity")
            .value(true)
            .description("Should the activity be cancelled")
            .popular(true)
            .refToView(Arrays.asList("frame", "frame2"))
            .build();

    private final Property initiatorpackage = Property.builder()
            .id("initiator")
            .type(Property.STRING)
            .title("Initiator")
            .value("")
            .description("Initiator of the process.")
            .popular(true)
            .build();

    private final Property textpackage = Property.builder()
            .id("text")
            .type(Property.STRING)
            .title("Text")
            .value("")
            .description("The text of the text annotation.")
            .popular(true)
            .refToView(Arrays.asList("text"))
            .build();

    private final Property multiinstance_typepackage = Property.builder()
            .id("multiinstance_type")
            .type(Property.KISBPM_MULTIINSTANCE)
            .title("Multi-instance type")
            .value("None")
            .description("Repeated activity execution (parallel or sequential) can be displayed through different loop types")
            .popular(true)
            .refToView(Arrays.asList("multiinstance"))
            .build();

    private final Property multiinstance_cardinalitypackage = Property.builder()
            .id("multiinstance_cardinality")
            .type(Property.STRING)
            .title("Cardinality (Multi-instance)")
            .value("")
            .description("Define the cardinality of multi instance.")
            .popular(true)
            .build();

    private final Property multiinstance_collectionpackage = Property.builder()
            .id("multiinstance_collection")
            .type(Property.STRING)
            .title("Collection (Multi-instance)")
            .value("")
            .description("Define the collection for the multi instance.")
            .popular(true)
            .build();

    private final Property multiinstance_variablepackage = Property.builder()
            .id("multiinstance_variable")
            .type(Property.STRING)
            .title("Element variable (Multi-instance)")
            .value("")
            .description("Define the element variable for the multi instance.")
            .popular(true)
            .build();

    private final Property multiinstance_conditionpackage = Property.builder()
            .id("multiinstance_condition")
            .type(Property.STRING)
            .title("Completion condition (Multi-instance)")
            .value("")
            .description("Define the completion condition for the multi instance.")
            .popular(true)
            .build();

    private final Property isforcompensationpackage = Property.builder()
            .id("isforcompensation")
            .type(Property.BOOLEAN)
            .title("Is for compensation")
            .value(false)
            .description("A flag that identifies whether this activity is intended for the purposes of compensation.")
            .popular(false)
            .refToView(Arrays.asList("compensation"))
            .build();

    private final Property sequencefloworderpackage = Property.builder()
            .id("sequencefloworder")
            .type(Property.COMPLEX)
            .title("Flow order")
            .value("")
            .description("Order outgoing sequence flows.")
            .popular(true)
            .build();

    private final Property signaldefinitionspackage = Property.builder()
            .id("signaldefinitions")
            .type(Property.MULTIPLECOMPLEX)
            .title("Signal definitions")
            .value("")
            .description("Signal definitions")
            .popular(true)
            .build();

    private final Property messagedefinitionspackage = Property.builder()
            .id("messagedefinitions")
            .type(Property.MULTIPLECOMPLEX)
            .title("Message definitions")
            .value("")
            .description("Message definitions")
            .popular(true)
            .build();

    private final Property istransactionpackage = Property.builder()
            .id("istransaction")
            .type(Property.BOOLEAN)
            .title("Is a transaction sub process")
            .value(false)
            .description("A flag that identifies whether this sub process is of type transaction.")
            .popular(true)
            .refToView(Arrays.asList("border"))
            .build();

}
