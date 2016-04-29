/* Copyright 2014 Mozilla Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

if (!PDFJS.PDFViewer || !PDFJS.getDocument) {
  alert('Please build the pdfjs-dist library using\n' +
        '  `gulp dist`');
}

// The workerSrc property shall be specified.
//
PDFJS.workerSrc = 'file:///android_asset/build/pdf.worker.js';

// Some PDFs need external cmaps.
//
// PDFJS.cMapUrl = '../../build/dist/cmaps/';
// PDFJS.cMapPacked = true;

var PAGE_TO_VIEW = 5;
var SCALE = 0.5;

var container = document.getElementById('pageContainer');

// (Optionally) enable hyperlinks within PDF files.
var pdfLinkService = new PDFJS.PDFLinkService();

var pdfViewer = new PDFJS.PDFViewer({
  container: container,
  linkService: pdfLinkService,
});
pdfLinkService.setViewer(pdfViewer);

// (Optionally) enable find controller.
var pdfFindController = new PDFJS.PDFFindController({
  pdfViewer: pdfViewer
});
pdfViewer.setFindController(pdfFindController);

container.addEventListener('pagesinit', function () {
  // We can use pdfViewer now, e.g. let's change default scale.
  pdfViewer.currentScaleValue = 'page-width';


    pdfFindController.executeCommand('find', {query: 'Совершенный'});

});

// Loading document.
PDFJS.getDocument(url).then(function (pdfDocument) {
  // Document loaded, specifying document for the viewer and
  // the (optional) linkService.
  pdfViewer.setDocument(pdfDocument);

  pdfLinkService.setDocument(pdfDocument, null);
});

function findWord(word){
    pdfFindController.executeCommand('find', {query: word});
}

PDFJS.getDocument(url).then(function (pdfDocument) {
  // Document loaded, retrieving the page.
  return pdfDocument.getPage(PAGE_TO_VIEW).then(function (pdfPage) {
    // Creating the page view with default parameters.
      var pdfPageView =  pdfPageView = new PDFJS.PDFPageView({
      container: container,
      id: PAGE_TO_VIEW,
      scale: SCALE,
      defaultViewport: pdfPage.getViewport(SCALE),
      // We can enable text/annotations layers, if needed
      textLayerFactory: new PDFJS.DefaultTextLayerFactory(),
      annotationLayerFactory: new PDFJS.DefaultAnnotationLayerFactory()
    });

    // Associates the actual page with the view, and drawing it
    pdfPageView.setPdfPage(pdfPage);
    pdfPageView.setFindController(new new PDFJS.PDFFindController());

    return pdfPageView.draw();
  });
});





function findWord(word){
    pdfFindController.executeCommand('find', {query: word});
}
