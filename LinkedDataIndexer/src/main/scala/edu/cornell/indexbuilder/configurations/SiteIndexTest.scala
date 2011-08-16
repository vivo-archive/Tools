package edu.cornell.indexbuilder.configurations
import edu.cornell.indexbuilder.CatalystDiscoverAndIndex
import edu.cornell.indexbuilder.CatalystDiscoverAndIndex

import edu.cornell.indexbuilder.VivoDiscoverAndIndex
import edu.cornell.indexbuilder.VitroVersion
import edu.cornell.indexbuilder.configurations.SiteIndexTest._

/*
 * This is a test of configurations to index
 * from the partener sites to a test server.
 */
object SiteIndexTest {
  val classUris = List( 
    """http://vivoweb.org/ontology/core#Postdoc""",
    """http://vivoweb.org/ontology/core#FacultyMember""",
    """http://vivoweb.org/ontology/core#Librarian"""
  )

  val uriToName = Map(
    "http://vivo.cornell.edu" -> "Cornell University",
    "http://vivo.med.cornell.edu" -> "Weill Cornell Medical College",
    "http://vivo.ufl.edu" -> "University of Florida",
    "http://vivo.iu.edu" -> "Indiana University",
    "http://vivo.wustl.edu" -> "Washington University School of Medicine",
    "http://vivo.psm.edu" -> "Ponce School of Medicine",
    "http://vivo.scripps.edu" -> "The Scripps Research Institute",
    "http://connects.catalyst.harvard.edu" -> "Harvard University Catalyst Profiles"
  )

  val solrUrl = "http://rollins.mannlib.cornell.edu:8080/devIndexUnstable/core3"    
}


object TestIndexCornell {
  val siteUrl = "http://vivo.cornell.edu"  
  val siteName = uriToName( siteUrl )
  val siteVivoVersion=VitroVersion.r1dot2

  def main(args : Array[String]) : Unit = {
    val process = new VivoDiscoverAndIndex(
      siteUrl,
      siteName,
      solrUrl,
      ClassLists.peopleOnlyClassList ,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexIndiana {
  val siteUrl = "http://vivo.iu.edu"
  val siteName = uriToName( siteUrl )
  val siteVivoVersion=VitroVersion.r1dot3

  def main(args : Array[String]) : Unit = {
    val process = new VivoDiscoverAndIndex(
      siteUrl,
      siteName,
      solrUrl,
      ClassLists.peopleOnlyClassList,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexPonce {
  val siteUrl = "http://vivo.psm.edu"
  val siteName = uriToName( siteUrl )
  val siteVivoVersion=VitroVersion.r1dot2

  def main(args : Array[String]) : Unit = {
    val process = new VivoDiscoverAndIndex(
      siteUrl,
      siteName,
      solrUrl,
      ClassLists.largeSiteClassList,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexScripps {
  val siteUrl = "http://vivo.scripps.edu"
  val siteName = uriToName( siteUrl )
  val siteVivoVersion=VitroVersion.r1dot2

  def main(args : Array[String]) : Unit = {
    val process = new VivoDiscoverAndIndex(
      siteUrl,
      siteName,
      solrUrl,
      ClassLists.largeSiteClassList,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexUFL {
  val siteUrl = "http://vivo.ufl.edu"
  val siteName = uriToName( siteUrl )
  val siteVivoVersion=VitroVersion.r1dot2

  def main(args : Array[String]) : Unit = {
    val process = new VivoDiscoverAndIndex(
      siteUrl,
      siteName,
      solrUrl,
      ClassLists.uflClasses,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexWustl {
  val siteUrl = "http://vivo.wustl.edu"
  val siteName = uriToName( siteUrl )
  val siteVivoVersion=VitroVersion.r1dot2

  def main(args : Array[String]) : Unit = {
    val process = new VivoDiscoverAndIndex(
      siteUrl,
      siteName,
      solrUrl,
      ClassLists.largeSiteClassList,
      siteVivoVersion
    )
    process.run()
  }
}

object TestIndexWeillMed {
  val siteUrl = "http://vivo.med.cornell.edu"
  val siteName = uriToName( siteUrl )
  val siteVivoVersion=VitroVersion.r1dot2

  def main(args : Array[String]) : Unit = {
    val process = new VivoDiscoverAndIndex(
      siteUrl,
      siteName,
      solrUrl,
      ClassLists.weillClasses,
      siteVivoVersion
    )
    process.run()
  }
}

object TestHarvard {
  val siteUrl = "http://connects.catalyst.harvard.edu"
  val siteName = uriToName( siteUrl )

  def main(args: Array[String]):Unit={
    val process = new CatalystDiscoverAndIndex(
      siteUrl,
      siteName,
      solrUrl,
      ClassLists.catalystClasses,
      "HarvardURIs.rdf"
    )
    process.run()
  }
}

private object ClassLists {

  val peopleOnlyClassList = List(
    //people 
    "http://vivoweb.org/ontology/core#FacultyMember", 
    "http://vivoweb.org/ontology/core#GraduateStudent", 
    "http://vivoweb.org/ontology/core#Librarian", 
    "http://vivoweb.org/ontology/core#NonAcademic", 
    "http://vivoweb.org/ontology/core#NonFacultyAcademic"
  )

  val catalystClasses = List(  
    "http://vivoweb.org/ontology/core#FacultyMember", 
    "http://vivoweb.org/ontology/core#GraduateStudent", 
    "http://vivoweb.org/ontology/core#Librarian", 
    "http://vivoweb.org/ontology/core#NonAcademic", 
    "http://vivoweb.org/ontology/core#NonFacultyAcademic"
  )

  val largeSiteClassList = List(
    //people 
    "http://vivoweb.org/ontology/core#FacultyMember", 
    "http://vivoweb.org/ontology/core#GraduateStudent", 
    "http://vivoweb.org/ontology/core#Librarian", 
    "http://vivoweb.org/ontology/core#NonAcademic", 
    "http://vivoweb.org/ontology/core#NonFacultyAcademic",     
    //Activities     
    "http://vivoweb.org/ontology/core#Agreement",     
    //Courses     
    "http://vivoweb.org/ontology/core#Course",     
    //Events     
    "http://purl.org/ontology/bibo/Conference", 
    "http://vivoweb.org/ontology/core#Presentation", 
    "http://purl.org/ontology/bibo/Workshop",     
    //Organizations     
    "http://vivoweb.org/ontology/core#AcademicDepartment", 
    "http://vivoweb.org/ontology/core#Association", 
    "http://vivoweb.org/ontology/core#Center", 
    "http://vivoweb.org/ontology/core#College", 
    "http://vivoweb.org/ontology/core#Committee", 
    "http://vivoweb.org/ontology/core#Department", 
    "http://vivoweb.org/ontology/core#Division", 
    "http://vivoweb.org/ontology/core#FundingOrganization", 
    "http://vivoweb.org/ontology/core#GovernmentAgency", 
    "http://xmlns.com/foaf/0.1/Group", 
    "http://vivoweb.org/ontology/core#Institute", 
    "http://vivoweb.org/ontology/core#Library", 
    "http://vivoweb.org/ontology/core#Museum", 
    "http://xmlns.com/foaf/0.1/Organization", 
    "http://vivoweb.org/ontology/core#Program", 
    "http://vivoweb.org/ontology/core#Publisher", 
    "http://vivoweb.org/ontology/core#School", 
    "http://vivoweb.org/ontology/core#Team", 
    "http://vivoweb.org/ontology/core#University",     
    //Research     
    "http://purl.org/ontology/bibo/AcademicArticle", 
    "http://purl.org/ontology/bibo/Article", 
    "http://purl.org/ontology/bibo/Book", 
    "http://purl.org/ontology/bibo/Chapter", 
    "http://vivoweb.org/ontology/core#ConferencePaper", 
    "http://purl.org/ontology/bibo/EditedBook", 
    "http://vivoweb.org/ontology/core#Grant", 
    "http://purl.org/ontology/bibo/Issue", 
    "http://purl.org/ontology/bibo/Journal", 
    "http://purl.org/ontology/bibo/Proceedings", 
    "http://purl.org/ontology/bibo/Report", 
    "http://vivoweb.org/ontology/core#Review", 
    "http://vivoweb.org/ontology/core#SubjectArea"
    //Locations     
    //"http://vivoweb.org/ontology/core#Country", 
    //"http://vivoweb.org/ontology/core#GeographicLocation"
    )

  
    val uflClasses = List(
      "http://vivo.ufl.edu/ontology/vivo-ufl/CourtesyFaculty", 
      "http://vivoweb.org/ontology/core#FacultyMember", 
      "http://vivoweb.org/ontology/core#GraduateStudent", 
      "http://vivoweb.org/ontology/core#Librarian", 
      "http://vivoweb.org/ontology/core#NonAcademic", 
      "http://vivoweb.org/ontology/core#NonFacultyAcademic", 
      "http://vivoweb.org/ontology/core#EmeritusProfessor", 

      "http://vivoweb.org/ontology/core#AcademicDepartment", 
      "http://vivoweb.org/ontology/core#Association", 
      "http://vivoweb.org/ontology/core#Center", 
      "http://vivoweb.org/ontology/core#ClinicalOrganization", 
      "http://vivoweb.org/ontology/core#College", 
      "http://vivoweb.org/ontology/core#Committee", 
      "http://vivoweb.org/ontology/core#Consortium", 
      "http://vivoweb.org/ontology/core#CoreLaboratory", 
      "http://vivoweb.org/ontology/core#Department", 
      "http://vivoweb.org/ontology/core#Division", 
      "http://vivoweb.org/ontology/core#ExtensionUnit", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalAcademicDepartment", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalCenter", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalCollege", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalCommittee", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalCoreLaboratory", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalDepartment", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalDivision", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalExtensionUnit", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalInstitute", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalLibrary", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalProgram", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalResearchLaboratory", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalSchool", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExternalStudentOrganization", 
      "http://vivoweb.org/ontology/core#Foundation", 
      "http://vivoweb.org/ontology/core#FundingOrganization", 
      "http://vivoweb.org/ontology/core#GovernmentAgency", 
      "http://xmlns.com/foaf/0.1/Group", 
      "http://vivoweb.org/ontology/core#Hospital", 
      "http://vivoweb.org/ontology/core#Institute", 
      "http://vivoweb.org/ontology/core#Laboratory", 
      "http://vivoweb.org/ontology/core#Library", 
      "http://vivoweb.org/ontology/core#Museum", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/NonAcademicDepartment", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/NonGovernmentalOrganization", 
      "http://xmlns.com/foaf/0.1/Organization", 
      "http://vivoweb.org/ontology/core#PrivateCompany", 
      "http://vivoweb.org/ontology/core#Program", 
      "http://vivoweb.org/ontology/core#Publisher", 
      "http://vivoweb.org/ontology/core#ResearchLaboratory", 
      "http://vivoweb.org/ontology/core#ResearchOrganization", 
      "http://vivoweb.org/ontology/core#School", 
      "http://vivoweb.org/ontology/core#StudentOrganization", 
      "http://vivoweb.org/ontology/core#Team", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFAcademicDepartment", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFAdministrativeUnit", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFCenter", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFCollege", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFCommittee", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFCoreLaboratory", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFDepartment", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFDivision", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFExtensionUnit", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFInstitute", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFLibrary", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFProgram", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFResearchLaboratory", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFSchool", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/UFStudentOrganization", 
      "http://vivoweb.org/ontology/core#University", 

      "http://purl.org/ontology/bibo/AcademicArticle", 
      "http://purl.org/ontology/bibo/Article", 
      "http://vivoweb.org/ontology/core#BlogPosting", 
      "http://purl.org/ontology/bibo/Book", 
      "http://vivoweb.org/ontology/core#CaseStudy", 
      "http://vivoweb.org/ontology/core#Catalog", 
      "http://purl.org/ontology/bibo/Chapter", 
      "http://vivoweb.org/ontology/core#ConferencePaper", 
      "http://vivoweb.org/ontology/core#ConferencePoster", 
      "http://purl.org/ontology/bibo/EditedBook", 
      "http://vivoweb.org/ontology/core#EditorialArticle", 
      "http://vivo.ufl.edu/ontology/vivo-ufl/ExtensionDocument", 
      "http://vivoweb.org/ontology/core#Grant", 
      "http://purl.obolibrary.org/obo/ERO_0000015", 
      "http://purl.org/ontology/bibo/Journal", 
      "http://vivoweb.org/ontology/core#Newsletter", 
      "http://vivoweb.org/ontology/core#NewsRelease", 
      "http://purl.org/ontology/bibo/Patent", 
      "http://purl.org/ontology/bibo/Proceedings", 
      "http://purl.org/ontology/bibo/Report", 
      "http://vivoweb.org/ontology/core#Review", 
      "http://purl.org/ontology/bibo/Series", 
      "http://vivoweb.org/ontology/core#Software", 
      "http://vivoweb.org/ontology/core#SubjectArea", 
      "http://purl.org/ontology/bibo/Thesis", 
      "http://vivoweb.org/ontology/core#Video", 
      "http://purl.org/ontology/bibo/Website", 

      "http://vivoweb.org/ontology/core#Grant", 

      "http://vivoweb.org/ontology/core#Agreement", 
      "http://vivoweb.org/ontology/core#Grant", 
      "http://vivoweb.org/ontology/core#Project", 
      "http://purl.obolibrary.org/obo/ERO_0000014", 
      "http://vivoweb.org/ontology/core#Service", 

      "http://vivoweb.org/ontology/core#Course", 

      "http://vivoweb.org/ontology/core#Competition", 
      "http://purl.org/ontology/bibo/Conference", 
      "http://vivoweb.org/ontology/core#Exhibit", 
      "http://vivoweb.org/ontology/core#InvitedTalk", 
      "http://vivoweb.org/ontology/core#Meeting", 
      "http://purl.org/ontology/bibo/Performance", 
      "http://vivoweb.org/ontology/core#Presentation", 
      "http://vivoweb.org/ontology/core#SeminarSeries", 
      "http://purl.org/ontology/bibo/Workshop", 
      "http://vivoweb.org/ontology/core#WorkshopSeries", 

      "http://vivoweb.org/ontology/core#Building", 
      "http://vivoweb.org/ontology/core#PopulatedPlace", 
      "http://vivoweb.org/ontology/core#Country", 
      "http://vivoweb.org/ontology/core#County", 
      "http://vivoweb.org/ontology/core#GeographicLocation", 
      "http://vivoweb.org/ontology/core#Room", 
      "http://vivoweb.org/ontology/core#StateOrProvince")

    val cornellClasses = List(
      "http://vivo.library.cornell.edu/ns/0.1#CornellAcademicEmployee", 
      "http://vivo.library.cornell.edu/ns/0.1#CornellAcademicStaff", 
      "http://vivo.library.cornell.edu/ns/0.1#CornellAlumnus", 
      "http://vivo.cornell.edu/ns/hr/0.9/hr.owl#CornellEmeritusProfessor", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#CornellFaculty", 
      "http://vivoweb.org/ontology/core#FacultyMember", 
      "http://vivoweb.org/ontology/core#GraduateStudent", 
      "http://vivoweb.org/ontology/core#Librarian", 
      "http://vivoweb.org/ontology/core#NonAcademic", 
      "http://vivoweb.org/ontology/core#NonFacultyAcademic", 
      "http://xmlns.com/foaf/0.1/Person", 
      "http://vivoweb.org/ontology/core#Postdoc", 
      "http://vivoweb.org/ontology/core#EmeritusProfessor", 

      "http://vivo.cornell.edu/ns/mannadditions/0.1#AcademicInitiative", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#AcademicThemeProjectInitiative", 
      "http://vivo.library.cornell.edu/ns/0.1#AdministrativeService", 
      "http://vivoweb.org/ontology/core#Agreement", 
      "http://vivo.library.cornell.edu/ns/0.1#AreasofConcentration", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#BusinessRelatedService", 
      "http://vivoweb.org/ontology/activity-insight#ConcentrationArea", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#ConsultationService", 
      "http://vivoweb.org/ontology/core#Contract", 
      "http://vivo.library.cornell.edu/ns/0.1#DemonstrationGrantProgram", 
      "http://vivoweb.org/ontology/activity-insight#EditChair", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#ExternalResearchService", 
      "http://vivo.library.cornell.edu/ns/0.1#FederalFormulaFundGrant", 
      "http://vivo.library.cornell.edu/ns/0.1#FundingOpportunitiesService", 
      "http://vivo.library.cornell.edu/ns/0.1#InterdisciplinaryInitiative", 
      "http://vivo.library.cornell.edu/ns/0.1#InternshipProgram", 
      "http://vivo.library.cornell.edu/ns/0.1#LibraryConsultingService", 
      "http://vivo.library.cornell.edu/ns/0.1#LibraryGeneralService", 
      "http://vivo.library.cornell.edu/ns/0.1#LibraryServiceGrouping", 
      "http://vivo.library.cornell.edu/ns/0.1#NLSIGenomicsFocusArea", 
      "http://vivo.library.cornell.edu/ns/0.1#OldLectureSeminarOrColloquium", 
      "http://vivoweb.org/ontology/activity-insight#PriorityArea", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#PrizeCompetition", 
      "http://vivoweb.org/ontology/core#Project", 
      "http://vivoweb.org/ontology/activity-insight#ImpactProject", 
      "http://vivo.library.cornell.edu/ns/0.1#ResearchGrant", 
      "http://vivo.library.cornell.edu/ns/0.1#ResearchGrantProgram", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#ResearchGroup", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#ResearchProject", 
      "http://vivo.library.cornell.edu/ns/0.1#ResearchRelatedService", 
      "http://vivo.library.cornell.edu/ns/0.1#ResearchServiceCompilation", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#Scholarship-Fellowship-TrainingGrant-Program", 
      "http://vivoweb.org/ontology/core#Service", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#TrainingGrant", 

      "http://vivo.library.cornell.edu/ns/0.1#SemesterClass", 
      "http://vivoweb.org/ontology/core#Course", 
      "http://vivo.library.cornell.edu/ns/0.1#CourseListings", 

      "http://vivo.library.cornell.edu/ns/0.1#AcademicWorkshopOrTrainingEvent", 
      "http://purl.org/ontology/bibo/Conference", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#CornellBusinessPlanContest", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#EntrepreneurshipEvent", 
      "http://vivo.library.cornell.edu/ns/0.1#EventSeries", 
      "http://vivoweb.org/ontology/core#Exhibit", 
      "http://vivo.library.cornell.edu/ns/0.1#exhibitSeries", 
      "http://vivoweb.org/ontology/core#InvitedTalk", 
      "http://vivo.library.cornell.edu/ns/0.1#LectureSeminarOrColloquium", 
      "http://vivo.library.cornell.edu/ns/0.1#LibraryInstructionalEvent", 
      "http://vivoweb.org/ontology/core#Meeting", 
      "http://vivo.library.cornell.edu/ns/0.1#OnlineExhibit", 
      "http://purl.org/ontology/bibo/Performance", 
      "http://vivoweb.org/ontology/core#Presentation", 
      "http://vivo.library.cornell.edu/ns/0.1#PublicOutreachWorkshop", 
      "http://vivo.library.cornell.edu/ns/0.1#SeminarOrLectureSeries", 
      "http://vivoweb.org/ontology/core#SeminarSeries", 
      "http://purl.org/ontology/bibo/Workshop", 
      "http://vivoweb.org/ontology/core#WorkshopSeries", 

      "http://vivo.library.cornell.edu/ns/0.1#AcademicDegreeGrantingAgent", 
      "http://vivoweb.org/ontology/core#AcademicDepartment", 
      "http://vivo.library.cornell.edu/ns/0.1#AcademicProgramOffice", 
      "http://vivo.library.cornell.edu/ns/0.1#AdministrativeUnit", 
      "http://vivo.library.cornell.edu/ns/0.1#Archives", 
      "http://vivoweb.org/ontology/core#Association", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#BusinessAndTechnologyPark", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#ClinicalSection", 
      "http://vivoweb.org/ontology/activity-insight#CollaborativeEntity", 
      "http://vivoweb.org/ontology/core#College", 
      "http://vivo.library.cornell.edu/ns/0.1#CollegeOrSchoolWithinUniversity", 
      "http://vivoweb.org/ontology/core#Committee", 
      "http://vivo.library.cornell.edu/ns/0.1#ServiceLaboratory", 
      "http://vivoweb.org/ontology/core#CoreLaboratory", 
      "http://vivo.library.cornell.edu/ns/0.1#AcademicDivision", 
      "http://vivo.library.cornell.edu/ns/0.1#CornellAcademicUnit", 
      "http://vivo.library.cornell.edu/ns/0.1#CornellCollegeOrProfessionalSchool", 
      "http://vivo.library.cornell.edu/ns/0.1#ExtensionResearchProgramUnit", 
      "http://vivo.library.cornell.edu/ns/0.1#FieldFacility", 
      "http://vivo.library.cornell.edu/ns/0.1#CornellLibrary", 
      "http://vivo.library.cornell.edu/ns/0.1#CornellResearchProgramUnitOrCenter", 
      "http://vivoweb.org/ontology/core#Department", 
      "http://vivoweb.org/ontology/core#Division", 
      "http://vivoweb.org/ontology/core#ExtensionUnit", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#ExternalDepartment", 
      "http://vivo.library.cornell.edu/ns/0.1#ExternalResearchProgramUnitOrCenter", 
      "http://vivo.library.cornell.edu/ns/0.1#ExternalUniversityOrIndependentCollege", 
      "http://vivoweb.org/ontology/core#Foundation", 
      "http://vivoweb.org/ontology/core#FundingOrganization", 
      "http://vivoweb.org/ontology/core#GovernmentAgency", 
      "http://vivo.library.cornell.edu/ns/0.1#GovernmentResearchLab", 
      "http://vivo.library.cornell.edu/ns/0.1#GraduateField", 
      "http://vivo.library.cornell.edu/ns/0.1#grantFundingAgent", 
      "http://xmlns.com/foaf/0.1/Group", 
      "http://vivoweb.org/ontology/core#Hospital", 
      "http://vivo.library.cornell.edu/ns/0.1#IndependentMuseum", 
      "http://vivo.library.cornell.edu/ns/0.1#IndependentResearchInstitute", 
      "http://vivoweb.org/ontology/core#Institute", 
      "http://vivoweb.org/ontology/core#Laboratory", 
      "http://vivoweb.org/ontology/core#Library", 
      "http://vivo.library.cornell.edu/ns/0.1#LibraryDepartment", 
      "http://vivoweb.org/ontology/core#Museum", 
      "http://vivo.library.cornell.edu/ns/0.1#ExternalCollegeOrSchoolWithinUniversity", 
      "http://xmlns.com/foaf/0.1/Organization", 
      "http://vivoweb.org/ontology/core#PrivateCompany", 
      "http://vivoweb.org/ontology/core#Program", 
      "http://vivoweb.org/ontology/core#Publisher", 
      "http://vivo.library.cornell.edu/ns/0.1#ResearchConsortium", 
      "http://vivoweb.org/ontology/core#ResearchOrganization", 
      "http://vivoweb.org/ontology/core#School", 
      "http://vivo.library.cornell.edu/ns/0.1#SocialNetwork", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#StudentClub", 
      "http://vivo.library.cornell.edu/ns/0.1#StudentOrganization", 
      "http://vivoweb.org/ontology/core#StudentOrganization", 
      "http://vivoweb.org/ontology/core#Team", 
      "http://vivoweb.org/ontology/core#University", 

      "http://vivoweb.org/ontology/core#Equipment", 

      "http://purl.org/ontology/bibo/AcademicArticle", 
      "http://purl.org/ontology/bibo/Article", 
      "http://purl.org/ontology/bibo/AudioDocument", 
      "http://vivo.library.cornell.edu/ns/0.1#BibliographicDatabase", 
      "http://vivoweb.org/ontology/core#BlogPosting", 
      "http://purl.org/ontology/bibo/Book", 
      "http://vivo.library.cornell.edu/ns/0.1#BookChapter", 
      "http://vivo.library.cornell.edu/ns/0.1#CALSImpactStatement", 
      "http://vivoweb.org/ontology/core#CaseStudy", 
      "http://purl.org/ontology/bibo/Chapter", 
      "http://purl.org/ontology/bibo/CollectedDocument", 
      "http://purl.org/ontology/bibo/Collection", 
      "http://vivoweb.org/ontology/core#ConferencePaper", 
      "http://vivoweb.org/ontology/core#ConferencePoster", 
      "http://vivo.library.cornell.edu/ns/0.1#DigitalCollection", 
      "http://vivo.library.cornell.edu/ns/0.1#DigitalResource", 
      "http://purl.org/ontology/bibo/DocumentPart", 
      "http://purl.org/ontology/bibo/EditedBook", 
      "http://vivoweb.org/ontology/core#EditorialArticle", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#FundingOpportunitiesDatabase", 
      "http://vivoweb.org/ontology/core#Grant", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#ImpactStatement", 
      "http://purl.org/ontology/bibo/Issue", 
      "http://purl.org/ontology/bibo/Journal", 
      "http://vivo.library.cornell.edu/ns/0.1#LibraryGuide", 
      "http://vivo.library.cornell.edu/ns/0.1#LibrarySubjectCollection", 
      "http://vivoweb.org/ontology/activity-insight#MediaContribution", 
      "http://vivo.library.cornell.edu/ns/0.1#NewsFeatureArticle", 
      "http://vivoweb.org/ontology/core#Newsletter", 
      "http://vivoweb.org/ontology/core#NewsRelease", 
      "http://vivo.library.cornell.edu/ns/0.1#OnlineResearchTool", 
      "http://vivo.library.cornell.edu/ns/0.1#OnlineResourceCompilation", 
      "http://vivo.library.cornell.edu/ns/0.1#OnlineTutorial", 
      "http://purl.org/ontology/bibo/Proceedings", 
      "http://vivo.library.cornell.edu/ns/0.1#RecentJournalArticle", 
      "http://purl.org/ontology/bibo/Report", 
      "http://vivoweb.org/ontology/core#Review", 
      "http://purl.org/ontology/bibo/Series", 
      "http://vivoweb.org/ontology/core#Software", 
      "http://vivoweb.org/ontology/core#SubjectArea", 
      "http://purl.org/ontology/bibo/Thesis", 
      "http://vivoweb.org/ontology/core#Video", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#VideoClip", 
      "http://vivo.library.cornell.edu/ns/0.1#VisualDatabase", 
      "http://vivo.library.cornell.edu/ns/0.1#WebResource", 
      "http://purl.org/ontology/bibo/Website", 

      "http://vivo.library.cornell.edu/ns/0.1#AcademicPriorityArea", 
      "http://vivo.library.cornell.edu/ns/0.1#CalsResearchArea", 
      "http://vivoweb.org/ontology/activity-insight#ContributionArea", 
      "http://vivo.library.cornell.edu/ns/0.1#EpsResearchArea", 
      "http://vivo.library.cornell.edu/ns/0.1#HumanitiesResearchArea", 
      "http://vivo.library.cornell.edu/ns/0.1#Keyword", 
      "http://vivo.library.cornell.edu/ns/0.1#Language", 
      "http://vivoweb.org/ontology/activity-insight#USDAArea", 
      "http://vivo.library.cornell.edu/ns/0.1#VetResearchArea", 

      "http://vivo.library.cornell.edu/ns/0.1#Auditorium", 
      "http://vivoweb.org/ontology/core#Building", 
      "http://vivo.library.cornell.edu/ns/0.1#ComputerLab", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#ConferenceRoom", 
      "http://vivoweb.org/ontology/core#Continent", 
      "http://vivoweb.org/ontology/core#Country", 
      "http://vivoweb.org/ontology/core#County", 
      "http://vivo.library.cornell.edu/ns/0.1#DomesticGeographicalRegion", 
      "http://vivo.library.cornell.edu/ns/0.1#Gallery", 
      "http://vivo.library.cornell.edu/ns/0.1#GenericFacility", 
      "http://www.aktors.org/ontology/portal#Geographical-Region", 
      "http://vivoweb.org/ontology/core#GeographicLocation", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#LectureHall", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#MultipurposeRoom", 
      "http://vivo.library.cornell.edu/ns/0.1#Neighborhood", 
      "http://vivo.library.cornell.edu/ns/0.1#NewYorkStateGeographicalRegion", 
      "http://vivoweb.org/ontology/core#PopulatedPlace", 
      "http://vivoweb.org/ontology/core#Room", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#RoomOrHall", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#SeminarRoom", 
      "http://vivoweb.org/ontology/core#StateOrProvince", 
      "http://vivo.cornell.edu/ns/mannadditions/0.1#TheaterOrAuditorium", 
      "http://vivo.library.cornell.edu/ns/0.1#VideoconferenceFacilities"
    )

    val weillClasses = List(
      "http://vivoweb.org/ontology/core#FacultyMember",
      "http://vivoweb.org/ontology/core#Librarian",
      "http://vivoweb.org/ontology/core#NonAcademic",
      "http://vivoweb.org/ontology/core#NonFacultyAcademic",
      "http://xmlns.com/foaf/0.1/Person",
      "http://vivoweb.org/ontology/core#Postdoc",
      "http://vivoweb.org/ontology/core#EmeritusProfessor",

      "http://vivoweb.org/ontology/core#Agreement",
      "http://weill.cornell.edu/vivo/ontology/wcmc#ClinicalTrialAgreement",
      "http://vivoweb.org/ontology/core#Contract",
      "http://weill.cornell.edu/vivo/ontology/wcmc#SponsoredResearchAgreement",

      "http://vivoweb.org/ontology/core#Course",

      "http://vivoweb.org/ontology/core#AcademicDepartment",
      "http://weill.cornell.edu/vivo/ontology/wcmc#AcademicProgram",
      "http://vivoweb.org/ontology/core#Center",
      "http://vivoweb.org/ontology/core#ClinicalOrganization",
      "http://vivoweb.org/ontology/core#College",
      "http://vivoweb.org/ontology/core#Consortium",
      "http://vivoweb.org/ontology/core#CoreLaboratory",
      "http://vivoweb.org/ontology/core#Department",
      "http://vivoweb.org/ontology/core#Division",
      "http://vivoweb.org/ontology/core#FundingOrganization",
      "http://vivoweb.org/ontology/core#Hospital",
      "http://vivoweb.org/ontology/core#Institute",
      "http://vivoweb.org/ontology/core#Library",
      "http://weill.cornell.edu/vivo/ontology/wcmc#Office",
      "http://xmlns.com/foaf/0.1/Organization",
      "http://vivoweb.org/ontology/core#Program",
      "http://vivoweb.org/ontology/core#University",

      "http://purl.org/ontology/bibo/AcademicArticle",
      "http://purl.org/ontology/bibo/Article",
      "http://purl.org/ontology/bibo/Book",
      "http://vivoweb.org/ontology/core#CaseStudy",
      "http://vivoweb.org/ontology/core#ConferencePaper",
      "http://vivoweb.org/ontology/core#EditorialArticle",
      "http://vivoweb.org/ontology/core#Grant",
      "http://purl.org/ontology/bibo/Journal",
      "http://purl.org/ontology/bibo/Proceedings",
      "http://vivoweb.org/ontology/core#Review",

      "http://vivoweb.org/ontology/core#Country",
      "http://vivoweb.org/ontology/core#GeographicLocation")

  }
