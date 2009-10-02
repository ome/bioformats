/* OME Schema Display Script
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * Copyright (C) 2003-2009 Open Microscopy Environment
 *       Massachusetts Institute of Technology,
 *       National Institutes of Health,
 *       University of Dundee,
 *       University of Wisconsin at Madison
 *
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Written by:  Will Moore
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
jQuery(function($) {

    // this is a dummy element to put in place of any element that is not found by reference. Text is set accordingly
    $("#header").append("<div id='notFound' class='namedElement'></div>");

    // start processing refs at the root element...
    var $root = $("#OME");

    $(this).processRef($root);

    $(this).processLink("extension");

    $(this).processLink("type");

    //	$(this).processLink("baseType");
    // collapse docs after building the hierarchy.
    $(".doc").hide();

    // collapse the elements that have been moved into hierarchy.
    $(".linkedElement").hide();

    // !! bug hack!!
    // TODO: Fix! These are not incorporated into the hierarchy. Hide them instead!
    //$("#ROIRef").hide();
    //	$("#Institution").hide();
    //	$("#DisplayOptionsID").hide();
    //	$("#ModuleID").hide();
    // Edit header to add controls.
    $("#header").append("Key: <span class='element'>Element</span> <span class='attribute'>Attribute *required</span> ");
    $("#header").append("<span class='baseType'>Base-type</span> <span class='restriction'>Restriction</span> ");
    $("#header").append("<a href='#' id='expandAll'>[expand all]</a> | <a href='#' id='collapseAll'>[collapse all]</a> ");
    $("#header").append(" | Documentation: <a href='#' id='showDocs'>show</a> / <a href='#' id='hideDocs'>hide</a>");

    // functions for toggling ALL refs etc from the controls above.
    $("#expandAll").click(function(e) {
        e.preventDefault();
        $(".linkedElement").show();
        // don't expand type references
    });
    $("#collapseAll").click(function(e) {
        e.preventDefault();
        $(".linkedElement").hide();
        //$(".type").hide();
    });
    $("#showDocs").click(function(e) {
        e.preventDefault();
        $(".doc").show();
    });
    $("#hideDocs").click(function(e) {
        e.preventDefault();
        $(".doc").hide();
    });

    // function for expanding/collapsing a single reference
    $(".ref").click(function() {
        // this is where the reference is displayed
        var $refDisplay = $(this).parent().next();
        $refDisplay.toggle();
    });

    // function for expanding/collapsing a single reference
    $(".type").click(function() {
        // this is where the reference is displayed
        var $refDisplay = $(this).parent().next();
        $refDisplay.toggle();
    });

    // function for expanding/collapsing a base type
    $(".extension").click(function() {
        // this is where the reference is displayed
        var $refDisplay = $(this).parent().next();
        $refDisplay.toggle();
    });

});

// functions
 (function($) {


    // function for processing a ref.
    $.fn.processRef = function($element) {

        // process refs to other top level elements
        var $refs = $element.find(".ref");
        $refs.each(function() {

            var ref = $(this).attr("ref");

            // this is where we want to append the reffed elements.
            var $refDisplay = $(this).parent();

            // find (by ID) and append the element we are referencing
            var $refElement = $("#" + ref);

            if ($refElement.length == 0) {
                $refElement = $("#notFound");
                $refElement.text(ref + " NOT FOUND");
            }

            $(this).appendElement($refElement, $refDisplay)

            // process any refs that the appended element may have
            $(this).processRef($refElement);
        });
    }

    // function for appending one element to another - copies element if already appended
    $.fn.processLink = function(className) {

        $("." + className).each(function(i) {

            var ref = $(this).attr("ref");

            // don't try to find element referenced by xsd:
            if (ref.substring(0, 3) == "xsd") {
                $(this).remove();
            } else {

                // this is where we want to append the reffed elements.
                var $refDisplay = $(this).parent();

                //alert("processLink: " + i + " " + className + " with ID: " + ref);
                //find and append the element we are referencing
                var $refElement = $("#" + ref);
                if ($refElement.length == 0) {
                    alert("processLink NOT FOUND: " + i + " '" + className + "' with ID: " + ref);
                    $refElement = $("#notFound");
                    $refElement.text(ref + " NOT FOUND");
                }
                $(this).appendElement($refElement, $refDisplay);
            }
        });

    }

    // function for appending one element to another - copies element if already appended
    $.fn.appendElement = function($refElement, $refDisplay) {

        // check if it has been reffed already
        if ($refElement.attr("refAdded") == "true") {

            // if already used, need to clone (and remove id)?
            var $clonedElement = $refElement.clone();
            $clonedElement.attr("id", null);
            $clonedElement.insertAfter($refDisplay);
            $clonedElement.attr("class", "linkedElement");
        } else {
            $refElement.attr("refAdded", "true");
            $refElement.insertAfter($refDisplay);
            $refElement.attr("class", "linkedElement");
        }
    }

    // function for appending one element to another - copies element if already appended
    $.fn.replaceElement = function($refElement, $refDisplay) {

        $refElement.attr("class", "childContainer");

        // check if it has been reffed already
        if ($refElement.attr("refAdded") == "true") {

            // if already used, need to clone (and remove id)?
            var $clonedElement = $refElement.clone();
            $clonedElement.attr("id", null);
            $refDisplay.replaceWith($clonedElement);

        } else {
            $refElement.attr("refAdded", "true");
            $refDisplay.replaceWith($clonedElement);
        }
    }

})(jQuery);