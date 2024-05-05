import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenericVisualizer {

    public void getAllClassesInFolder(File folder, List<ClassOrInterfaceDeclaration> classes)
    {
        if(folder.isFile())
        {
            try {
                classes.addAll(StaticJavaParser.parse(folder).findAll(ClassOrInterfaceDeclaration.class));
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
        else if (folder.isDirectory())
        {
            File[] files = folder.listFiles();
            if(files != null)
            {
                for(File f : files)
                {
                    getAllClassesInFolder(f, classes);
                }
            }
        }
    }

    public Type replaceTypesInType(Type t, NodeList<TypeParameter> oldTypes, NodeList<TypeParameter> newTypes)
    {
        if(t.isPrimitiveType())
        {
            return t.clone();
        }
        else if (t.isArrayType())
        {
            t.arra
        }
        else
        {
            throw new UnsupportedOperationException("Type not supported!");
        }
    }

    public ClassOrInterfaceDeclaration createGenericType(ClassOrInterfaceDeclaration genClass, NodeList<TypeParameter> newTypes)
    {
        if(!genClass.isGeneric())
        {
            return genClass;
        }
        else
        {
            //System.out.println("Generic class:");
        }

        ClassOrInterfaceDeclaration result = new ClassOrInterfaceDeclaration();

        NodeList<TypeParameter> genTypes = genClass.getTypeParameters();
        List<String> genTypeNames = genClass.getTypeParameters().stream().map(NodeWithSimpleName::getNameAsString).collect(Collectors.toList());
        while(newTypes.size() < genTypeNames.size())
        {
            newTypes.add(new TypeParameter("Object"));
        }
        result.setTypeParameters(new NodeList<>());

        String className = new String(genClass.getNameAsString());
        className += "_";
        for(TypeParameter t : newTypes)
        {
            className += t.getNameAsString();
        }
        result.setName(className);

        for(BodyDeclaration<?> bodyDec : genClass.getMembers())
        {
            if(bodyDec.isFieldDeclaration())
            {
                FieldDeclaration fieldDec = bodyDec.asFieldDeclaration();
                FieldDeclaration resultDec = fieldDec.clone();
                for(VariableDeclarator varDec : resultDec.getVariables())
                {
                    if(genTypeNames.contains(varDec.getTypeAsString()))
                    {
                        varDec.setType(newTypes.get(genTypeNames.indexOf(varDec.getTypeAsString())));
                    }
                }
                result.addMember(resultDec);
            }
            else if(bodyDec.isConstructorDeclaration())
            {
                var constDec = bodyDec.asConstructorDeclaration();
                var resultDec = constDec.clone();
                NodeList<Parameter> params = resultDec.getParameters();
                for(Parameter p : params)
                {
                    if(genTypeNames.contains(p.getTypeAsString()))
                    {
                        p.setType(newTypes.get(genTypeNames.indexOf(p.getTypeAsString())));
                    }

                }
                result.addMember(resultDec);
            }
            else if (bodyDec.isMethodDeclaration())
            {
                var methDec = bodyDec.asMethodDeclaration();
                var resultDec = methDec.clone();
                for(Parameter p : resultDec.getParameters())
                {
                    if(genTypeNames.contains(p.getTypeAsString()))
                    {
                        p.setType(newTypes.get(genTypeNames.indexOf(p.getTypeAsString())));
                    }
                }

                if(genTypeNames.contains(resultDec.getTypeAsString()))
                {
                    resultDec.setType(newTypes.get(genTypeNames.indexOf(resultDec.getTypeAsString())));
                }
                result.addMember(resultDec);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        //System.out.println("Hello World!");

        File f = new File("src/main/testclasses/");
        List<ClassOrInterfaceDeclaration> fileClasses = new ArrayList<>();
        GenericVisualizer genV = new GenericVisualizer();

        genV.getAllClassesInFolder(f, fileClasses);


        for(int i = 0; i < fileClasses.size(); i++)
        {
            boolean generic = fileClasses.get(i).isGeneric();

            if(generic)
            {
                var result = genV.createGenericType(fileClasses.get(i), new NodeList<>(new TypeParameter("Integer")));
                System.out.println(result);
                result = genV.createGenericType(fileClasses.get(i), new NodeList<>(new TypeParameter("String")));
                System.out.println(result);
                result = genV.createGenericType(fileClasses.get(i), new NodeList<>());
                System.out.println(result);
                System.out.println(fileClasses.get(i));
            }
        }
    }
}
