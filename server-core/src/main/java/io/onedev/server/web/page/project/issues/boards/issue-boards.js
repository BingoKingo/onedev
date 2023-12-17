onedev.server.issueBoards = {
	onColumnDomReady: function(containerId, callback) {
		var $body = $("#" + containerId);

		if (callback) {
			$body.droppable({
				accept: function($draggable) {
					return $draggable.is(".issue-boards .board-card") && !$draggable.is("#"+containerId + " .board-card");
				}, 
				drop: function(event, ui) {
					if ($body.hasClass("issue-droppable")) {
						ui.draggable.data("droppedTo", this);
						var issue = ui.draggable.data("issue");
						if (!ui.draggable.parent().is(this))
							callback(issue);
					}
				}
			});
		}
	}, 
	onCardDomReady: function(cardId, callback) {
		var $card = $("#" + cardId);
        var $container = $card.closest(".columns");
		var containerContentWidth;
		if (callback) {
			$card.draggable({
				helper: "clone", 
				appendTo: $container,
				scroll: false,
				start: function(event, ui) {
					// pretend that we are in ajax operation to prevent websocket auto-update while dragging
					onedev.server.ajaxRequests.count++;		
					$card.addClass("issue-dragging");
					$(ui.helper).outerWidth($card.outerWidth());
					containerContentWidth = $container.prop("scrollWidth");
					callback($card.data("issue"));
				}, 
				drag: function(event, ui) {
					var uiHelperLeft = $(ui.helper).offset().left;
					var containerLeft = $container.offset().left;
					
					if (uiHelperLeft < containerLeft) { 
						var scrollLeft = $container.scrollLeft();
						var newScrollLeft = scrollLeft - (containerLeft - uiHelperLeft);
						if (newScrollLeft > 0)
							$container.scrollLeft(newScrollLeft);
						else
							$container.scrollLeft(0);
					}
					
					uiHelperLeft = $(ui.helper).offset().left;
					containerLeft = $container.offset().left;
					
					var uiHelperRight = uiHelperLeft + $(ui.helper).outerWidth();
					var containerWidth = $container.outerWidth();
					var containerRight = containerLeft + containerWidth;
					
					if (uiHelperRight > containerRight) {
						var scrollLeft = $container.scrollLeft();
						var newScrollLeft = scrollLeft + (uiHelperRight - containerRight);
						if (newScrollLeft <= containerContentWidth - containerWidth)
							$container.scrollLeft(newScrollLeft);
						else
							$container.scrollLeft(containerContentWidth - containerWidth);
					}
				},
				stop: function(event, ui) {
					var droppedTo = $card.data("droppedTo");
					if (droppedTo && !$card.parent().is(droppedTo)) {
						/*
						 * After dragging issues to a column, a dialog may open to ask for options. User may 
						 * cancel the dialog and the accept flag will be set to false 
						 */
						function checkAccepted() {
							var accepted = $(".issue-boards").data("accepted");
							if (accepted != undefined) {
								if (!accepted) {
									$card.removeClass("issue-dragging");
								}
								onedev.server.ajaxRequests.count--;
								$(".issue-boards").removeData("accepted");
							} else {
								setTimeout(checkAccepted, 100);
							}
						}
						checkAccepted();
					} else {
						$card.removeClass("issue-dragging");
						onedev.server.ajaxRequests.count--;
					}
					$card.removeData("droppedTo");
					$(".issue-boards .body .body").removeClass("issue-droppable");
				}
			});
		}
	} 
}