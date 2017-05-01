<#compress>
{
    "AlfrescoNumDocs" : ${searcher1['numDocs']?c},
    "AlfrescoMaxDocs" : ${searcher1['maxDoc']?c},
    "AlfrescoDeletedDocs" : ${searcher1['deletedDocs']?c},
    "ArchiveNumDocs" : ${searcher2['numDocs']?c},
    "ArchiveMaxDocs" : ${searcher2['maxDoc']?c},
    "ArchiveDeletedDocs" : ${searcher2['deletedDocs']?c},
    "AlfrescoOnDiskBytes" : ${searcher1['sizeInBytes']?c},
    "ArchiveOnDiskBytes" : ${searcher2['sizeInBytes']?c},
    "AlfrescoHeapBytes" : ${searcher1['indexHeapUsageBytes']?c},
    "ArchiveHeapBytes" : ${searcher2['indexHeapUsageBytes']?c}  
}
</#compress>