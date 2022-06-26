package generator.processors;

import org.eclipse.xtend.lib.macro.AbstractClassProcessor;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend2.lib.StringConcatenationClient;

@SuppressWarnings("all")
public class EntityProcessor extends AbstractClassProcessor {
  public void createFindByIdMethodBody(final MutableMethodDeclaration it, final MutableClassDeclaration entity) {
    StringConcatenationClient _client = new StringConcatenationClient() {
      @Override
      protected void appendTo(TargetStringConcatenation _builder) {
        _builder.append("return ");
        String _lowerCase = entity.getSimpleName().toLowerCase();
        _builder.append(_lowerCase);
        _builder.append("Repository.findById(id);");
        _builder.newLineIfNotEmpty();
      }
    };
    it.setBody(_client);
  }
  
  public void createGetAllMethodBody(final MutableMethodDeclaration it, final MutableClassDeclaration entity) {
    StringConcatenationClient _client = new StringConcatenationClient() {
      @Override
      protected void appendTo(TargetStringConcatenation _builder) {
        _builder.append("return Optional.of((List<");
        String _simpleName = entity.getSimpleName();
        _builder.append(_simpleName);
        _builder.append(">) ");
        String _lowerCase = entity.getSimpleName().toLowerCase();
        _builder.append(_lowerCase);
        _builder.append("Repository.findAll());");
        _builder.newLineIfNotEmpty();
      }
    };
    it.setBody(_client);
  }
  
  public void createAddNewMethodBody(final MutableMethodDeclaration it, final MutableClassDeclaration entity) {
    StringConcatenationClient _client = new StringConcatenationClient() {
      @Override
      protected void appendTo(TargetStringConcatenation _builder) {
        String _simpleName = entity.getSimpleName();
        _builder.append(_simpleName);
        _builder.append(" new");
        String _lowerCase = entity.getSimpleName().toLowerCase();
        _builder.append(_lowerCase);
        _builder.append(" = new ");
        String _simpleName_1 = entity.getSimpleName();
        _builder.append(_simpleName_1);
        _builder.append("(");
        String _lowerCase_1 = entity.getSimpleName().toLowerCase();
        _builder.append(_lowerCase_1);
        _builder.append("DTO);");
        _builder.newLineIfNotEmpty();
        _builder.append("return Optional.ofNullable(");
        String _lowerCase_2 = entity.getSimpleName().toLowerCase();
        _builder.append(_lowerCase_2);
        _builder.append("Repository.save(new");
        String _lowerCase_3 = entity.getSimpleName().toLowerCase();
        _builder.append(_lowerCase_3);
        _builder.append("));");
        _builder.newLineIfNotEmpty();
      }
    };
    it.setBody(_client);
  }
  
  public void createGetEntityMethodBody(final MutableMethodDeclaration it, final MutableClassDeclaration entity) {
    StringConcatenationClient _client = new StringConcatenationClient() {
      @Override
      protected void appendTo(TargetStringConcatenation _builder) {
        _builder.append("return ");
        String _lowerCase = entity.getSimpleName().toLowerCase();
        _builder.append(_lowerCase);
        _builder.append("Service.get");
        String _simpleName = entity.getSimpleName();
        _builder.append(_simpleName);
        _builder.append("(id).map(ResponseEntity::ok)");
        _builder.newLineIfNotEmpty();
        _builder.append("                            ");
        _builder.append(".orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());");
        _builder.newLine();
      }
    };
    it.setBody(_client);
  }
  
  public void createCONTROLLERGetAllBody(final MutableMethodDeclaration it, final MutableClassDeclaration entity) {
    StringConcatenationClient _client = new StringConcatenationClient() {
      @Override
      protected void appendTo(TargetStringConcatenation _builder) {
        _builder.append("return ");
        String _lowerCase = entity.getSimpleName().toLowerCase();
        _builder.append(_lowerCase);
        _builder.append("Service.getAll().map(ResponseEntity::ok)");
        _builder.newLineIfNotEmpty();
        _builder.append("                            ");
        _builder.append(".orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());");
        _builder.newLine();
      }
    };
    it.setBody(_client);
  }
  
  public void createCONTROLLERAddNewBody(final MutableMethodDeclaration it, final String parameterName, final MutableClassDeclaration entity) {
    StringConcatenationClient _client = new StringConcatenationClient() {
      @Override
      protected void appendTo(TargetStringConcatenation _builder) {
        _builder.append("return ");
        String _lowerCase = entity.getSimpleName().toLowerCase();
        _builder.append(_lowerCase);
        _builder.append("Service.add");
        String _simpleName = entity.getSimpleName();
        _builder.append(_simpleName);
        _builder.append("(");
        _builder.append(parameterName);
        _builder.append(").map(e -> new ResponseEntity<>(e, HttpStatus.CREATED))");
        _builder.newLineIfNotEmpty();
        _builder.append("                                ");
        _builder.append(".orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());");
        _builder.newLine();
      }
    };
    it.setBody(_client);
  }
}
