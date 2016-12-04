package SHACL
package messages

import vocabulary.RDF

object ShapeParserMessages {
  val invalidShTargetNode: String =
    "Invalid `sh:targetNode`."

  val invalidShTargetClass: String =
    "Invalid `sh:targetClass`."

  val invalidShTargetSubjectsOf: String =
    "Invalid `sh:targetSubjectsOf`."

  val invalidShTargetObjectsOf: String =
    "Invalid `sh:targetObjectsOf`."

  val shPredicateMustBeIRI: String =
    s"`sh:predicate` must be an IRI."

  val shPathMustBeIRIOrBlankNode: String =
    s"`sh:path` must be an IRI or blank node."

  val shPredicateMustNotBeRDFnil: String =
    s"`sh:predicate` must not be ${RDF.nil}."

  val shPathMustNotBeRDFnil: String =
    s"`sh:path` must not be ${RDF.nil}."

  val moreThanOneShPredicate: String =
    s"Found more than one `sh:predicate`."

  val moreThanOneShPath: String =
    s"Found more than one `sh:path`."

  val moreThanOneShInversePath: String =
    s"Found more than one `sh:inversePath`."

  val emptyShProperty: String =
    s"Empty `sh:property`."

  val shPropertyMustBeBlankNode: String =
    "`sh:property` must be a blank node."

  val oneOrMoreTriples: String =
    "One or more triples."

  val shNodeKindMustBeShBlankNodeOrShIRIOrShLiteral: String =
    "`sh:nodeKind` must be `sh:BlankNode`, `sh:IRI`, or `sh:Literal`."

  val shInMustHaveOneOrMoreValues: String =
    "`sh:in` must have one or more values."

  val shClassMustBeIRI: String =
    "`sh:class` must be an IRI."

  val shClassMustNotBeRDFnil: String =
    s"`sh:class` must not be ${RDF.nil}."

  val shDatatypeMustBeIRI: String =
    "`sh:datatype` must be an IRI."

  val shDatatypeMustNotBeRDFnil: String =
    s"`sh:datatype` must not be ${RDF.nil}."

  val shMinLengthMustBeNumber: String =
    "`sh:minLength` must be a number."

  val shMaxLengthMustBeNumber: String =
    "`sh:maxLength` must be a number."

  val rdfFirstMustBeIRI: String =
    "`rdf:first` must be an IRI."

  val rdfRestMustBeIRI: String =
    "`rdf:rest` must be an IRI."

  val expectingTwoIRIsFoundOne: String =
    "Expecting two IRIs but found only one IRI."

  val expectingTwoIRIsFoundMoreThanTwo: String =
    "Expecting only two IRIs but found more than two IRIs."

  val shInversePathMustNotBeRDFnil: String =
    s"`sh:inversePath` must not be ${RDF.nil}."

  val shInversePathMustBeIRI: String =
    "`sh:inversePath` must be an IRI."

  val shAlternativePathMustBeBNode: String =
    "`sh:alternativePath` must be a BNode."

  val expectingPathNameFoundNone: String =
    "Expecting a path name but found none."

  val shZeroOrMorePathMustNotBeRDFnil: String =
    s"`sh:zeroOrMorePath` must not be ${RDF.nil}."

  val shZeroOrMorePathMustBeIRI: String =
    "`sh:zeroOrMorePath` must be an IRI."

  val shOneOrMorePathMustNotBeRDFnil: String =
    s"`sh:oneOrMorePath` must not be ${RDF.nil}."

  val shOneOrMorePathMustBeIRI: String =
    "`sh:oneOrMorePath` must be an IRI."

  val shZeroOrOnePathMustNotBeRDFnil: String =
    s"`sh:zeroOrOnePath` must not be ${RDF.nil}."

  val shZeroOrOnePathMustBeIRI: String =
    "`sh:zeroOrOnePath` must be an IRI."

  val invalidSPARQLpath: String =
    "Invalid SPARQL path."
}
