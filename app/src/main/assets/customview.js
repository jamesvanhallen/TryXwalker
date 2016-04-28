  // NOTE: 
    // Modifying the URL below to another server will likely *NOT* work. Because of browser
    // security restrictions, we have to use a file server with special headers
    // (CORS) - most servers don't support cross-origin browser requests.
    //

    //
    // Disable workers to avoid yet another cross-origin issue (workers need the URL of
    // the script to be loaded, and currently do not allow cross-origin scripts)
    //
    if (!PDFJS.PDFViewer || !PDFJS.getDocument) {
      alert('Please build the library and components using\n' +
            '  `gulp generic components`');
    } else {
        alert('good');
    }

//    //'use strict';
//   //PDFJS.disableWorker = false;
   PDFJS.workerSrc = 'file:///android_asset/build/pdf.worker.js';


    var pdfDoc = null,
        pageNum = 1,
        pageRendering = false,
        pageNumPending = null,
        scale = 2.0;
        canvas = document.getElementById('the-canvas'),
        ctx = canvas.getContext('2d');


       var container = document.getElementById('viewerContainer');


       var pdfLinkService = new PDFJS.PDFLinkService();

       var pdfViewer = new PDFJS.PDFViewer({
         container: container,
         linkService: pdfLinkService,
       });
       pdfLinkService.setViewer(pdfViewer);

       // Link server is not so nessasory
       var pdfFindController = new PDFJS.PDFFindController({
         pdfViewer: pdfViewer
       });
       pdfViewer.setFindController(pdfFindController);

       container.addEventListener('pagesinit', function () {
         pdfViewer.currentScaleValue = 'page-width';

       });


    /**
     * Get page info from document, resize canvas accordingly, and render page.
     * @param num Page number.
     */
    function renderPage(num) {
      pageRendering = true;
      // Using promise to fetch the page
      pageNum = num;
      pdfDoc.getPage(num).then(function(page) {
        var viewport = page.getViewport(scale);
        canvas.height = viewport.height;
        canvas.width = viewport.width;

        // Render PDF page into canvas context
        var renderContext = {
          canvasContext: ctx,
          viewport: viewport
        };
        var renderTask = page.render(renderContext);

        // Wait for rendering to finish
        renderTask.promise.then(function () {
          pageRendering = false;
          if (pageNumPending !== null) {
            // New page rendering is pending
            renderPage(pageNumPending);
            pageNumPending = null;
          }
        });
      });

      //alerts for pages displaying
      alert("_total"+pdfDoc.numPages);
      alert("_cur"+pageNum);

    }

    /**
     * If another page rendering in progress, waits until the rendering is
     * finished. Otherwise, executes rendering immediately.
     */
    function queueRenderPage(num) {
      if (pageRendering) {
        pageNumPending = num;
      } else {
        renderPage(num);
      }
    }

    /**
     * Displays previous page.
     */
    function onPrevPage() {
      if (pageNum <= 1) {
        return;
      }
      pageNum--;
      queueRenderPage(pageNum);
    }

    /**
     * Displays next page.
     */
    function onNextPage() {
      if (pageNum >= pdfDoc.numPages) {
        return;
      }
      pageNum++;
      queueRenderPage(pageNum);
    }


    PDFJS
    .getDocument(url)
    .then(function getPdf(pdfDoc_) {
         pdfDoc = pdfDoc_;
            pdfViewer.setDocument(pdfDoc_);
          //  pdfLinkService.setDocument(pdfDoc_, null);
            renderPage(pageNum);
    });

    function findWord(word){
       // pdfDoc.getPage(3).then(getPageText);
       pdfFindController.executeCommand('find', {query: word});

    }

    //deprecated
    function getPageText(page) {
       var textContent = page.getTextContent();
                 alert(textContent.bidiTexts===null);
                 alert(JSON.stringify(textContent));
                 if( null != textContent.bidiTexts ){
                   var page_text = "";
                   var last_block = null;
                   for( var k = 0; k < textContent.bidiTexts.length; k++ ){
                       var block = textContent.bidiTexts[k];
                       if( last_block != null && last_block.str[last_block.str.length-1] != ' '){
                           if( block.x < last_block.x )
                               page_text += "\r\n";
                           else if ( last_block.y != block.y && ( last_block.str.match(/^(\s?[a-zA-Z])$|^(.+\s[a-zA-Z])$/) == null ))
                               page_text += ' ';
                       }
                       page_text += block.str;
                       last_block = block;
                   }
                   alert("_pagetext" + page_text);
                }
    }





    
   