'use strict';

if (!PDFJS.PDFViewer || !PDFJS.getDocument) {
    alert('Please build the pdfjs-dist library using\n' +
        '  `gulp dist`');
}

// The workerSrc property shall be specified.
//
PDFJS.workerSrc = 'build/pdf.worker.js';

// Some PDFs need external cmaps.
//
// PDFJS.cMapUrl = '../../build/dist/cmaps/';
// PDFJS.cMapPacked = true;



var SCALE = 1.0;
var query = null;
var isDrawAllow = true;
var currentPage;

var container = document.getElementById('pageContainer');
var container2 = document.getElementById('page');
var next_page = document.getElementById('next');
next_page.addEventListener("click", function(){nextPage()}, false);

var pdfViewer = {
    pdfDocument: null,
    pageViews: [],
    get pagesCount() { return this.pdfDocument.numPages; },
    currentPageNumber: PAGE_TO_VIEW,
    getPageTextContent: function (index) { return this.pdfDocument.getPage(index + 1).then(function (page) {
        return page.getTextContent();}); },
    scrollPageIntoView: function (dest) {
        console.log(dest);

        if(PAGE_TO_VIEW!==dest){
            destroyPage();
            PAGE_TO_VIEW = dest;
            container2.innerHTML = "";
            drawPage();
        }


    },
    getPageView: function (index) { return this.pageViews[PAGE_TO_VIEW]; }
};

var pdfFindController = new PDFJS.PDFFindController({
    pdfViewer: pdfViewer
});

var textLayerFactory = {
    createTextLayerBuilder: function (textLayerDiv, pageIndex, viewport) {
        return new PDFJS.TextLayerBuilder({
            textLayerDiv: textLayerDiv,
            pageIndex: pageIndex,
            viewport: viewport,
            findController: pdfFindController
        });
    }
};

// Loading document.

drawPage();

function drawPage(){
    PDFJS.getDocument(url).then(function (pdfDocument) {
        pdfViewer.pdfDocument = pdfDocument;
        pdfFindController.resolveFirstPage();



        // Document loaded, retrieving the page.
        return pdfDocument.getPage(PAGE_TO_VIEW).then(function (pdfPage) {
            // Creating the page view with default parameters.
            currentPage = pdfPage;
            var pdfPageView = new PDFJS.PDFPageView({
                container: container2,
                id: PAGE_TO_VIEW,
                scale: SCALE,
                defaultViewport: currentPage.getViewport(SCALE),
                // We can enable text/annotations layers, if needed
                textLayerFactory: textLayerFactory,
                annotationLayerFactory: new PDFJS.DefaultAnnotationLayerFactory()
            });
            pdfViewer.pageViews[PAGE_TO_VIEW] = pdfPageView;
            // Associates the actual page with the view, and drawing it
            pdfPageView.setPdfPage(currentPage);
           
            isDrawAllow = true;
            return pdfPageView.draw();
        });
    });
}

function findWord(word){
    query = word;
    pdfFindController.executeCommand('find', {query: word});
}

function findNext(){
    pdfFindController.executeCommand('findagain', {query: query});
}


function findPrev(){
    pdfFindController.executeCommand('findagain', {query: query, findPrevious: true});
}

function nextPage(){
    if(PAGE_TO_VIEW<pdfViewer.pdfDocument.numPages && isDrawAllow){
        destroyPage();
        isDrawAllow = false;
        container2.innerHTML = "";
        PAGE_TO_VIEW++;
        drawPage();
    }

}

function prevPage(){

    if(PAGE_TO_VIEW>1 && isDrawAllow){
        destroyPage();
        isDrawAllow = false;
        container2.innerHTML = "";
        PAGE_TO_VIEW--;
        drawPage();
    }

}

function scrollToPage(page){
    destroyPage();
    container2.innerHTML = "";
    PAGE_TO_VIEW = page;
    drawPage();
}


function destroyPage() {
    currentPage.destroy();
}
