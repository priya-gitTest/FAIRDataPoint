/**
 * The MIT License
 * Copyright © 2017 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.dtls.fairdatapoint.api.controller.metadata.catalog;

import nl.dtls.fairmetadata4j.model.CatalogMetadata;
import nl.dtls.fairdatapoint.api.controller.metadata.MetadataController;
import nl.dtls.fairdatapoint.api.dto.member.MemberCreateDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static nl.dtls.fairdatapoint.util.IRIUtils.removeLastPartOfIRI;

@RestController
@RequestMapping("/catalog/{id}/members")
public class CatalogMemberController extends MetadataController {

    @Autowired
    private MemberService memberService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MemberDTO>> getMembers(HttpServletRequest request) throws
            ResourceNotFoundException, MetadataServiceException {
        // Get catalog
        IRI uri = getRequestURLasIRI(request);
        IRI catalogUri = removeLastPartOfIRI(uri);
        CatalogMetadata metadata = catalogMetadataService.retrieve(catalogUri);

        // Get members
        String catalogId = metadata.getIdentifier().getIdentifier().getLabel();
        List<MemberDTO> dto = memberService.getMembers(catalogId, CatalogMetadata.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userUuid}", method = RequestMethod.PUT)
    public ResponseEntity<MemberDTO> putMember(@PathVariable final String userUuid,
                                               @RequestBody @Valid MemberCreateDTO reqDto,
                                               HttpServletRequest request) throws
            ResourceNotFoundException, MetadataServiceException {
        // Get catalog
        IRI uri = getRequestURLasIRI(request);
        IRI catalogUri = removeLastPartOfIRI(removeLastPartOfIRI(uri));
        CatalogMetadata metadata = catalogMetadataService.retrieve(catalogUri);

        // Create / Update member
        String catalogId = metadata.getIdentifier().getIdentifier().getLabel();
        MemberDTO dto = memberService.createOrUpdateMember(catalogId, CatalogMetadata.class, userUuid,
                reqDto.getMembershipUuid());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userUuid}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMember(@PathVariable final String userUuid, HttpServletRequest request) throws ResourceNotFoundException, MetadataServiceException {
        // Get catalog
        IRI uri = getRequestURLasIRI(request);
        IRI catalogUri = removeLastPartOfIRI(removeLastPartOfIRI(uri));
        CatalogMetadata metadata = catalogMetadataService.retrieve(catalogUri);

        // Delete member
        String catalogId = metadata.getIdentifier().getIdentifier().getLabel();
        memberService.deleteMember(catalogId, CatalogMetadata.class, userUuid);
        return ResponseEntity.noContent().build();
    }

}