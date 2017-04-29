<#compress>
{
    "AlfrescoNumDocs" : ${searcher1['numDocs']?c},
    "AlfrescoMaxDocs" : ${searcher1['maxDoc']?c},
    "AlfrescoDeletedDocs" : ${searcher1['deletedDocs']?c},
    "ArchiveNumDocs" : ${searcher2['numDocs']?c},
    "ArchiveMaxDocs" : ${searcher2['maxDoc']?c},
    "ArchiveDeletedDocs" : ${searcher2['deletedDocs']?c},
    "AlfrescoOnDiskGb" : ${summary.alfresco['On disk (GB)']?replace(",",".")},
    "ArchiveOnDiskGb" : ${summary.archive['On disk (GB)']?replace(",",".")}
}
</#compress>